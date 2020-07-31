package cn.stylefeng.guns.sys.modular.third.service;

import cn.stylefeng.guns.base.consts.ConstantsContext;
import cn.stylefeng.guns.sys.core.exception.OutExcetion;
import cn.stylefeng.guns.sys.modular.third.entity.BaseMessage;
import cn.stylefeng.guns.sys.modular.third.entity.Textcard;
import cn.stylefeng.guns.sys.modular.third.entity.TextcardMessage;
import cn.stylefeng.guns.sys.modular.third.model.AuthWeChatRequest;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;



/**@desc  : 发送消息
 *
 * @author: shirayner
 * @date  : 2017-8-18 上午10:06:23
 */
public class SendMessageUtil {

    //主动发送消息接口
    private static final String SEND_NOTICE_URL="https://qyapi.weixin.qq.com/cgi-bin/message/send?access_token=ACCESS_TOKEN";

    private static final String SEND_GET_ACCTOKEN = "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=CORPID&corpsecret=SECRET";

    /**
     * @desc ：0.发送企业通知消息
     *
     * @param accessToken
     * @param message
     * @throws Exception void
     */
    public static void sendNotice(String accessToken,BaseMessage message) {
        if(accessToken == null || accessToken == ""){
            try {
                JSONObject jsonObject = HttpHelper.doGet(SEND_GET_ACCTOKEN.replace("CORPID", ConstantsContext.getWeChatClientId()).replace("SECRET", ConstantsContext.getWeChatSerect()));
                accessToken = jsonObject.getString("access_token");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //1.准备POST请求参数,将javaBean 转为 json
        Object data= JSON.toJSON(message);
        System.out.println(data);

        //2.获取请求url
        String url=SEND_NOTICE_URL.replace("ACCESS_TOKEN", accessToken);

        //3.发起POST请求，获取返回结果
        JSONObject jsonObject= null;
        try {
            jsonObject = HttpHelper.doPost(url, data);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        if(jsonObject == null){
            return;
        }
        System.out.println("jsonObject:"+jsonObject.toString());

        //4.解析结果，获取
        if (null != jsonObject) {

            //5.错误消息处理
            if (0 != jsonObject.getInteger("errcode")) {
                int errCode = jsonObject.getInteger("errcode");
                String errMsg = jsonObject.getString("errmsg");
                throw new IllegalArgumentException(errMsg);
            }
        }
    }


   /*     public void sendTextNotice(String accessToken,String userId,String content) throws Exception {
            //1.创建消息
            TextMessage message=new TextMessage();
            //1.1必需
            message.setMsgtype("text");
            message.setAgentid(Env.AGENT_ID);

            //1.2非必需
            message.setTouser(userId);

            //1.3消息内容
            Text text=new Text();
            text.setContent(content);
            message.setText(text);

            //2.发送消息
            this.sendNotice(accessToken, message);
        }
*/
    /**  2.发送文本卡片消息
     *
     * @param accessToken
     * @param userId
     * @param title   标题
     * @param description  描述，即text
     * @param url          点击后跳转的链接。
     * @throws Exception
     */
    public static void sendTextCardNotice(String accessToken,String userId,String title,String description,String url) {
        //1.创建消息
        TextcardMessage message=new TextcardMessage();
        //1.1必需
        message.setMsgtype("textcard");
        message.setAgentid(ConstantsContext.getWeChatAgentId());
        //1.2非必需
        message.setTouser(userId);

        //1.3消息内容
        Textcard textcard=new Textcard();
        textcard.setTitle(title);
        textcard.setDescription(description);
        textcard.setUrl(url);

        message.setTextcard(textcard);

        //2.发送消息
        sendNotice(accessToken, message);
    }

    /**
     * 推送消息
     */
    public static void sendWeChatMsg(String userId,String title,String description,String url){

        sendTextCardNotice(AuthWeChatRequest.accessTokens,userId,title,description,url);
    }
        /*public void sendImgNotice(String accessToken,String userId,String mediaId) throws Exception {
            ImgMessage message=new ImgMessage();
            //1.1必需
            message.setMsgtype("image");
            message.setAgentid(Env.AGENT_ID);
            //1.2非必需
            //不区分大小写
            message.setTouser(userId);
            //message.setToparty("1");
            //message.setTotag(totag);
            //message.setSafe(0);

            //1.3消息内容
            Media image=new Media();
            image.setMedia_id(mediaId);
            message.setImage(image);

            //2.发送消息
            this.sendNotice(accessToken, message);
        }
*/


}
