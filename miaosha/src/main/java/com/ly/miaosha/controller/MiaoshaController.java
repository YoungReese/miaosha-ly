package com.ly.miaosha.controller;

import com.ly.miaosha.access.AccessLimit;
import com.ly.miaosha.domain.MiaoshaOrder;
import com.ly.miaosha.domain.MiaoshaUser;
import com.ly.miaosha.domain.OrderInfo;
import com.ly.miaosha.rabbitmq.MQSender;
import com.ly.miaosha.rabbitmq.MiaoshaMessage;
import com.ly.miaosha.redis.*;
import com.ly.miaosha.result.CodeMsg;
import com.ly.miaosha.result.Result;
import com.ly.miaosha.service.GoodsService;
import com.ly.miaosha.service.MiaoshaService;
import com.ly.miaosha.service.MiaoshaUserService;
import com.ly.miaosha.service.OrderService;
import com.ly.miaosha.vo.GoodsVo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/miaosha")
public class MiaoshaController implements InitializingBean {

    @Autowired
    MiaoshaUserService userService;

    @Autowired
    RedisService redisService;

    @Autowired
    GoodsService goodsService;

    @Autowired
    OrderService orderService;

    @Autowired
    MiaoshaService miaoshaService;

    @Autowired
    MQSender sender;

    private HashMap<Long, Boolean> localOverMap = new HashMap<>();

    /**
     * 让 MiaoshaController implements InitializingBean
     * 然后实现方法 afterPropertiesSet()
     * 系统初始化后会回调这个接口，我们可以在这个里面完成一些初始化的工作
     */
    public void afterPropertiesSet() {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        if (goodsList == null) {
            return;
        }
        for (GoodsVo goods : goodsList) {
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), goods.getStockCount());
            localOverMap.put(goods.getId(), false);
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    @ResponseBody
    public Result<Boolean> reset(Model model) {
        List<GoodsVo> goodsList = goodsService.listGoodsVo();
        for (GoodsVo goods : goodsList) {
            goods.setStockCount(10);
            redisService.set(GoodsKey.getMiaoshaGoodsStock, "" + goods.getId(), 10);
            localOverMap.put(goods.getId(), false);
        }
        redisService.delete(OrderKey.getMiaoshaOrderByUidGid);
        redisService.delete(MiaoshaKey.isGoodsOver);
        miaoshaService.reset(goodsList);
        return Result.success(true);
    }

    @RequestMapping("/do_miaosha2")
    public String list(Model model, MiaoshaUser user,
                       @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return "login";
        }
        // 判断库存
        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId);
        int stock = goods.getStockCount();
        if (stock <= 0) {
            model.addAttribute("errmsg", CodeMsg.MIAO_SHA_OVER.getMsg());
            return "miaosha_fail";
        }
        // 判断是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            model.addAttribute("errmsg", CodeMsg.REPEAT_MIAOSHA.getMsg());
            return "miaosha_fail";
        }
        // 减库存 下订单 写入秒杀订单
        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
        model.addAttribute("orderInfo", orderInfo);
        model.addAttribute("goods", goods);
        return "order_detail";
    }

    /**
     * rest 风格的秒杀接口
     */
    @RequestMapping(value = "/{path}/do_miaosha", method = RequestMethod.POST)
    @ResponseBody
    public Result<Integer> miaosha(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId, @PathVariable("path") String path) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

        // 验证 path
        boolean checked = miaoshaService.checkPath(user, goodsId, path);
        if (!checked) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        // 内存标记，减少 redis 访问
        boolean over = localOverMap.get(goodsId);
        if (over) {
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 预减库存
        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId); // 10
        if (stock < 0) {
            localOverMap.put(goodsId, true);
            return Result.error(CodeMsg.MIAO_SHA_OVER);
        }
        // 判断之前是否已经秒杀到了
        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
        if (order != null) {
            return Result.error(CodeMsg.REPEAT_MIAOSHA);
        }

        // 入队
        MiaoshaMessage mm = new MiaoshaMessage();
        mm.setUser(user);
        mm.setGoodsId(goodsId);
        sender.sendMiaoshaMessage(mm);
        return Result.success(0); // 排队中

//        // 验证 path
//        boolean check = miaoshaService.checkPath(user, goodsId, path);
//        if (!check) {
//            return Result.error(CodeMsg.REQUEST_ILLEGAL);
//        }
//        // 内存标记，减少 redis 访问
//        boolean over = localOverMap.get(goodsId);
//        if (over) {
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        // 预减库存
//        long stock = redisService.decr(GoodsKey.getMiaoshaGoodsStock, "" + goodsId);//10
//        if (stock < 0) {
//            localOverMap.put(goodsId, true);
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        // 判断是否已经秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null) {
//            return Result.error(CodeMsg.REPEATE_MIAOSHA);
//        }
//        // 入队
//        MiaoshaMessage mm = new MiaoshaMessage();
//        mm.setUser(user);
//        mm.setGoodsId(goodsId);
//        sender.sendMiaoshaMessage(mm);
//        return Result.success(0);//排队中

        /**
         * 改造前的老代码逻辑，这里不删除是为了和改造后的做对比
         */
//        // 判断库存
//        GoodsVo goods = goodsService.getGoodsVoByGoodsId(goodsId); // 10 个商品，req1 req2
//        int stock = goods.getStockCount();
//        if (stock <= 0) {
//            return Result.error(CodeMsg.MIAO_SHA_OVER);
//        }
//        // 判断是否已经秒杀到了
//        MiaoshaOrder order = orderService.getMiaoshaOrderByUserIdGoodsId(user.getId(), goodsId);
//        if (order != null) {
//            return Result.error(CodeMsg.REPEAT_MIAOSHA);
//        }
//        // 减库存 下订单 写入秒杀订单
//        OrderInfo orderInfo = miaoshaService.miaosha(user, goods);
//        return Result.success(orderInfo);
    }

    /**
     * 轮询接口，用于秒杀发送 mq 后，客户端验证是否秒杀成功
     * orderId：成功
     * -1：秒杀失败
     * 0： 排队中
     */
    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public Result<Long> miaoshaResult(Model model, MiaoshaUser user, @RequestParam("goodsId") long goodsId) {
        model.addAttribute("user", user);
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        long result = miaoshaService.getMiaoshaResult(user.getId(), goodsId);
        return Result.success(result);
    }

    /**
     * value = "verifyCode", defaultValue = "0"
     * 这里使用 default = 0 方便我们测试这个注解 @AccessLimit 的功能
     */
    @AccessLimit(seconds = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaPath(HttpServletRequest request, MiaoshaUser user,
                                         @RequestParam("goodsId") long goodsId,
                                         @RequestParam(value = "verifyCode", defaultValue = "0") int verifyCode) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }

//        // 查询次数限制，后续使用注解和拦截器的通用方式替代
//        String uri = request.getRequestURI();
//        String key = uri + "_" + user.getId();
//        Integer count = redisService.get(AccessKey.access, key, Integer.class);
//        if (count == null) {
//            redisService.set(AccessKey.access, key, 1);
//        } else if (count < 5) {
//            redisService.incr(AccessKey.access, key);
//        } else {
//            return Result.error(CodeMsg.ACCESS_LIMIT_REACHED);
//        }


        boolean pass = miaoshaService.checkVerifyCode(user, goodsId, verifyCode);
        if (!pass) {
            return Result.error(CodeMsg.REQUEST_ILLEGAL);
        }
        String path = miaoshaService.createMiaoshaPath(user, goodsId);
        return Result.success(path);
    }


    /**
     * 生成图片验证码接口
     */
    @RequestMapping(value = "/verifyCode", method = RequestMethod.GET)
    @ResponseBody
    public Result<String> getMiaoshaVerifyCode(HttpServletResponse response, MiaoshaUser user,
                                               @RequestParam("goodsId") long goodsId) {
        if (user == null) {
            return Result.error(CodeMsg.SESSION_ERROR);
        }
        try {
            BufferedImage image = miaoshaService.createVerifyImage(user, goodsId);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "JPEG", out);
            out.flush();
            out.close();
            return null; // 成功返回 null 是因为结果已经通过流返回出去了，所以这里可以直接返回 null
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error(CodeMsg.MIAOSHA_FAIL);
        }
    }
}
