package com.atguigu.sk; 


import com.atguigu.utils.JedisPoolUtil;
import jdk.nashorn.internal.scripts.JD;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

@Controller
public class SecondKillController {

    static String secKillScript = "local userid=KEYS[1];\r\n"
            + "local prodid=KEYS[2];\r\n"
            + "local qtkey='sk:'..prodid..\":qt\";\r\n" + "local usersKey='sk:'..prodid..\":usr\";\r\n"
            + "local userExists=redis.call(\"sismember\",usersKey,userid);\r\n"
            + "if tonumber(userExists)==1 then \r\n"
            + "   return 2;\r\n" + "end\r\n" + "local num= redis.call(\"get\" ,qtkey);\r\n"
            + "if tonumber(num)<=0 then \r\n" + "   return 0;\r\n" + "else \r\n"
            + "   redis.call(\"decr\",qtkey);\r\n"
            + "   redis.call(\"sadd\",usersKey,userid);\r\n"
            + "end\r\n" + "return 1";
    @ResponseBody
    @PostMapping(value = "/sk/doSecondKill",produces = "text/html;charset=UTF-8")
    public String doSKByLUA(Integer id){
        Integer userId = (int)(10000*Math.random());
        Jedis jedis = JedisPoolUtil.getJedisPoolInstance().getResource();
        String s = jedis.scriptLoad(secKillScript);
        Object evalsha = jedis.evalsha(s, 2, userId + "", id + "");
        int result = (int)((long)evalsha);
        if (result == 1){
            System.out.println("秒杀成功");
            jedis.close();
            return "ok";
        }else if (result == 2){
            System.out.println("用户已存在");
            jedis.close();
            return "重复秒杀";
        }else {
            System.out.println("库存不足");
            jedis.close();
            return "库存不足";
        }
    }


//
//    @ResponseBody
//    @PostMapping(value = "/sk/doSecondKill",produces = "text/html;charset=UTF-8")
//    public String doSK(Integer id){
//
//        Integer userId = (int)(10000*Math.random());
//
//        Integer pId = id;
//
//        String qtKey = "sk:"+pId+":qt";
//        String usersKey = "sk:"+pId+":user";
//
//        Jedis jedis = new Jedis("192.168.132.128", 6379);
//
//
//        String s = jedis.get(qtKey);
//        Boolean sismember = jedis.sismember(usersKey, userId + "");
//        if (sismember){
//            System.out.println("重复参与活动:"+userId);
//            return "已参与活动,请勿重复参与";
//        }
//
//        jedis.watch(qtKey);
//        if (StringUtils.isEmpty(s)){
//            return "秒杀尚未开始";
//        }
//
//        int i = Integer.parseInt(s);
//        System.out.println("库存为:"+i);
//        if (i<0){
//            System.err.println("库存不足");
//            return "库存不足";
//        }
//
//
//        Transaction multi = jedis.multi();
//        multi.decr(qtKey);
//        multi.sadd(usersKey,userId+"");
//        multi.exec();
//        System.out.println("秒杀成功:"+userId);
//        jedis.close();
//        return "ok";
//    }


}
