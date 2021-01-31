package com.dushuge.controller.model;

/**
 * 广告配置信息
 * 参数 JSON数据：{"media_type":1,"media_position":1,"position":1}
 * 请求地址：http://adspace.readxin.cn:1024/admessage/
 * media_type  1 广点通 2 穿山甲#媒体类型
 * media_position  1、小说 2、漫画  3、听书 #分类
 * position   1、开屏 2、列表页 3、详情页 4、发现页  4、阅读章节结尾 5、底部banner   #广告类型
 *
 * 返回JSON数据：{"position_id": 12345321, "media_id": 323225}
 * position_id 广告位ID
 * media_id    媒体ID
 *
 */
public class AdMediaBean {

    private int media_type;
    private int media_position;
    private int position;
    private String position_id;
    private String media_id;

    public AdMediaBean() {
    }

    public AdMediaBean(int media_type, int media_position, int position) {
        this.media_type = media_type;
        this.media_position = media_position;
        this.position = position;
    }

    public int getMedia_type() {
        return media_type;
    }

    public void setMedia_type(int media_type) {
        this.media_type = media_type;
    }

    public int getMedia_position() {
        return media_position;
    }

    public void setMedia_position(int media_position) {
        this.media_position = media_position;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getPosition_id() {
        return position_id;
    }

    public void setPosition_id(String position_id) {
        this.position_id = position_id;
    }

    public String getMedia_id() {
        return media_id;
    }

    public void setMedia_id(String media_id) {
        this.media_id = media_id;
    }

    @Override
    public String toString() {
        return "AdMediaBean{" +
                "media_type=" + media_type +
                ", media_position=" + media_position +
                ", position=" + position +
                '}';
    }


}
