package com.demo.api.entity;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * Created by mhh on 2017/3/25.
 */
@Entity
@Table(name = "e_entity")
public class EEntity {

    @Id
    private String receive_order_id = "";
    private String receive_order_row_shop_cut_form_id = "";
    private String receive_order_date = "";
    private String receive_order_consignee_name = "";
    private String receive_order_consignee_zip_code = "";
    private String receive_order_consignee_address1 = "";
    private String receive_order_consignee_address2 = "";

    private String c_address1 = "";
    private String c_address2 = "";
    private String c_address3 = "";
    private String c_address4 = "";
    private String c_address5 = "";

    private String receive_order_consignee_tel = "";
    private String receive_order_purchaser_mail_address = "";
    private String receive_order_purchaser_name = "";
    private String receive_order_purchaser_zip_code = "";
    private String receive_order_purchaser_address1 = "";
    private String receive_order_purchaser_address2 = "";

    private String p_address1 = "";
    private String p_address2 = "";
    private String p_address3 = "";
    private String p_address4 = "";
    private String p_address5 = "";

    private String receive_order_purchaser_tel = "";
    private String receive_order_total_amount = "";
    private String receive_order_hope_delivery_time_slot_id = "";
    private String receive_order_hope_delivery_time_slot_name = "";
    private String receive_order_point_amount = "";
    private String receive_order_other_amount = "";
    private String receive_order_row_no = "";
    private String receive_order_row_goods_id = "";
    private String receive_order_row_goods_name = "";
    private String receive_order_row_quantity = "";
    private String receive_order_row_unit_price = "";

    @Column(length = 16)
    private String status;              // 状态

    private int renum;                  // 上传次数

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_date")
    private Date updateDate;            //

    private String getTxt(String txt, int len) {
        if(StringUtils.isEmpty(txt)) {
            return "";
        }
        try {
            while (true) {
                if(txt.getBytes("GBK").length > len) {
                    txt = txt.substring(0, txt.length()-1);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return txt;
    }

    public String getSId() {
        if(this.getReceive_order_id() == null) {
            return "00000000"+1;
        }
        String id = String.valueOf(this.getReceive_order_id());
        int len = id.length();
        if(len < 9) {
            for(int i = len; i < 9; i++) {
                id = "0"+id;
            }
        }
        return id;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("H,A7354101,");
        buffer.append(getSId()).append(",");
        buffer.append(getTxt(receive_order_id, 30)).append(",");
        buffer.append(getTxt(receive_order_row_shop_cut_form_id, 30)).append(",");
        buffer.append(getTxt(receive_order_date,10)).append(",");
        buffer.append(getTxt(receive_order_consignee_name,30)).append(",");
        buffer.append(getTxt(receive_order_consignee_zip_code,8)).append(",");
        buffer.append(getTxt(c_address1,8)).append(",");
        buffer.append(getTxt(c_address2,24)).append(",");
        buffer.append(getTxt(c_address3,24)).append(",");
        buffer.append(getTxt(c_address4,32)).append(",");
        buffer.append(getTxt(c_address5,32)).append(",");
        buffer.append(getTxt(receive_order_consignee_name,50)).append(",");
        buffer.append(getTxt(receive_order_consignee_tel,60)).append(",");
        buffer.append(",");
        buffer.append(getTxt(receive_order_purchaser_mail_address,80)).append(",");
        buffer.append(",");
        buffer.append(getTxt(receive_order_purchaser_name,30)).append(",");
        buffer.append(getTxt(receive_order_purchaser_zip_code,8)).append(",");
        buffer.append(getTxt(p_address1,8)).append(",");
        buffer.append(getTxt(p_address2,24)).append(",");
        buffer.append(getTxt(p_address3,24)).append(",");
        buffer.append(getTxt(p_address4,32)).append(",");
        buffer.append(getTxt(p_address5,32)).append(",");
        buffer.append(getTxt(receive_order_purchaser_name,50)).append(",");
        buffer.append(getTxt(receive_order_purchaser_tel,60)).append(",");
        buffer.append(",");
        buffer.append(getTxt(receive_order_purchaser_mail_address,80)).append(",");
        buffer.append(",");
        buffer.append("9,");
        buffer.append(getTxt(receive_order_total_amount,10)).append(",");
        buffer.append("0,");
        buffer.append(getTxt(receive_order_total_amount,10)).append(",");
        buffer.append("680,");
        buffer.append(",");
        buffer.append("0,");
        buffer.append(getTxt(getHopeTime(receive_order_hope_delivery_time_slot_name),2)).append("0,");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append("1,");
        buffer.append(getTxt(receive_order_point_amount,8)).append(",");
        buffer.append("ポイント,");
        buffer.append(getTxt(receive_order_other_amount,8)).append(",");
        buffer.append("ポイント,");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append("\r");
        buffer.append("D,A7354101,");
        buffer.append(getTxt(receive_order_id,30)).append(",");
        buffer.append(getTxt(receive_order_row_no,5)).append(",");
        buffer.append(getTxt(receive_order_row_goods_id,20)).append(",");
        buffer.append(getTxt(receive_order_row_goods_name,170)).append(",");
        buffer.append(getTxt(receive_order_row_quantity,5)).append(",");
        buffer.append(getTxt(receive_order_row_unit_price,10)).append(",");
        buffer.append("0,");
        buffer.append(getTxt(receive_order_row_unit_price,10)).append(",");
        buffer.append(",");
        buffer.append(",");
        buffer.append(",");
        return buffer.toString();
    }

    private String getHopeTime(String txt) {
        if(StringUtils.isEmpty(txt)) {
            return "";
        }
        if("午前中".equals(txt)) {
            return "08";
        } else {
            return "12";
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRenum() {
        return renum;
    }

    public void setRenum(int renum) {
        this.renum = renum;
    }

    public String getC_address1() {
        return c_address1;
    }

    public void setC_address1(String c_address1) {
        this.c_address1 = c_address1;
    }

    public String getC_address2() {
        return c_address2;
    }

    public void setC_address2(String c_address2) {
        this.c_address2 = c_address2;
    }

    public String getC_address3() {
        return c_address3;
    }

    public void setC_address3(String c_address3) {
        this.c_address3 = c_address3;
    }

    public String getC_address4() {
        return c_address4;
    }

    public void setC_address4(String c_address4) {
        this.c_address4 = c_address4;
    }

    public String getC_address5() {
        return c_address5;
    }

    public void setC_address5(String c_address5) {
        this.c_address5 = c_address5;
    }

    public String getP_address1() {
        return p_address1;
    }

    public void setP_address1(String p_address1) {
        this.p_address1 = p_address1;
    }

    public String getP_address2() {
        return p_address2;
    }

    public void setP_address2(String p_address2) {
        this.p_address2 = p_address2;
    }

    public String getP_address3() {
        return p_address3;
    }

    public void setP_address3(String p_address3) {
        this.p_address3 = p_address3;
    }

    public String getP_address4() {
        return p_address4;
    }

    public void setP_address4(String p_address4) {
        this.p_address4 = p_address4;
    }

    public String getP_address5() {
        return p_address5;
    }

    public void setP_address5(String p_address5) {
        this.p_address5 = p_address5;
    }

    public String getReceive_order_id() {
        return receive_order_id;
    }

    public void setReceive_order_id(String receive_order_id) {
        this.receive_order_id = receive_order_id;
    }

    public String getReceive_order_row_shop_cut_form_id() {
        return receive_order_row_shop_cut_form_id;
    }

    public void setReceive_order_row_shop_cut_form_id(String receive_order_row_shop_cut_form_id) {
        this.receive_order_row_shop_cut_form_id = receive_order_row_shop_cut_form_id;
    }

    public String getReceive_order_date() {
        return receive_order_date;
    }

    public void setReceive_order_date(String receive_order_date) {
        this.receive_order_date = receive_order_date;
    }

    public String getReceive_order_consignee_name() {
        return receive_order_consignee_name;
    }

    public void setReceive_order_consignee_name(String receive_order_consignee_name) {
        this.receive_order_consignee_name = receive_order_consignee_name;
    }

    public String getReceive_order_consignee_zip_code() {
        return receive_order_consignee_zip_code;
    }

    public void setReceive_order_consignee_zip_code(String receive_order_consignee_zip_code) {
        this.receive_order_consignee_zip_code = receive_order_consignee_zip_code;
    }

    public String getReceive_order_consignee_address1() {
        return receive_order_consignee_address1;
    }

    public void setReceive_order_consignee_address1(String receive_order_consignee_address1) {
        this.receive_order_consignee_address1 = receive_order_consignee_address1;
    }

    public String getReceive_order_consignee_address2() {
        return receive_order_consignee_address2;
    }

    public void setReceive_order_consignee_address2(String receive_order_consignee_address2) {
        this.receive_order_consignee_address2 = receive_order_consignee_address2;
    }

    public String getReceive_order_consignee_tel() {
        return receive_order_consignee_tel;
    }

    public void setReceive_order_consignee_tel(String receive_order_consignee_tel) {
        this.receive_order_consignee_tel = receive_order_consignee_tel;
    }

    public String getReceive_order_purchaser_mail_address() {
        return receive_order_purchaser_mail_address;
    }

    public void setReceive_order_purchaser_mail_address(String receive_order_purchaser_mail_address) {
        this.receive_order_purchaser_mail_address = receive_order_purchaser_mail_address;
    }

    public String getReceive_order_purchaser_name() {
        return receive_order_purchaser_name;
    }

    public void setReceive_order_purchaser_name(String receive_order_purchaser_name) {
        this.receive_order_purchaser_name = receive_order_purchaser_name;
    }

    public String getReceive_order_purchaser_zip_code() {
        return receive_order_purchaser_zip_code;
    }

    public void setReceive_order_purchaser_zip_code(String receive_order_purchaser_zip_code) {
        this.receive_order_purchaser_zip_code = receive_order_purchaser_zip_code;
    }

    public String getReceive_order_purchaser_address1() {
        return receive_order_purchaser_address1;
    }

    public void setReceive_order_purchaser_address1(String receive_order_purchaser_address1) {
        this.receive_order_purchaser_address1 = receive_order_purchaser_address1;
    }

    public String getReceive_order_purchaser_address2() {
        return receive_order_purchaser_address2;
    }

    public void setReceive_order_purchaser_address2(String receive_order_purchaser_address2) {
        this.receive_order_purchaser_address2 = receive_order_purchaser_address2;
    }

    public String getReceive_order_purchaser_tel() {
        return receive_order_purchaser_tel;
    }

    public void setReceive_order_purchaser_tel(String receive_order_purchaser_tel) {
        this.receive_order_purchaser_tel = receive_order_purchaser_tel;
    }

    public String getReceive_order_total_amount() {
        return receive_order_total_amount;
    }

    public void setReceive_order_total_amount(String receive_order_total_amount) {
        this.receive_order_total_amount = receive_order_total_amount;
    }

    public String getReceive_order_hope_delivery_time_slot_id() {
        return receive_order_hope_delivery_time_slot_id;
    }

    public void setReceive_order_hope_delivery_time_slot_id(String receive_order_hope_delivery_time_slot_id) {
        this.receive_order_hope_delivery_time_slot_id = receive_order_hope_delivery_time_slot_id;
    }

    public String getReceive_order_hope_delivery_time_slot_name() {
        return receive_order_hope_delivery_time_slot_name;
    }

    public void setReceive_order_hope_delivery_time_slot_name(String receive_order_hope_delivery_time_slot_name) {
        this.receive_order_hope_delivery_time_slot_name = receive_order_hope_delivery_time_slot_name;
    }

    public String getReceive_order_point_amount() {
        return receive_order_point_amount;
    }

    public void setReceive_order_point_amount(String receive_order_point_amount) {
        this.receive_order_point_amount = receive_order_point_amount;
    }

    public String getReceive_order_other_amount() {
        return receive_order_other_amount;
    }

    public void setReceive_order_other_amount(String receive_order_other_amount) {
        this.receive_order_other_amount = receive_order_other_amount;
    }

    public String getReceive_order_row_no() {
        return receive_order_row_no;
    }

    public void setReceive_order_row_no(String receive_order_row_no) {
        this.receive_order_row_no = receive_order_row_no;
    }

    public String getReceive_order_row_goods_id() {
        return receive_order_row_goods_id;
    }

    public void setReceive_order_row_goods_id(String receive_order_row_goods_id) {
        this.receive_order_row_goods_id = receive_order_row_goods_id;
    }

    public String getReceive_order_row_goods_name() {
        return receive_order_row_goods_name;
    }

    public void setReceive_order_row_goods_name(String receive_order_row_goods_name) {
        this.receive_order_row_goods_name = receive_order_row_goods_name;
    }

    public String getReceive_order_row_quantity() {
        return receive_order_row_quantity;
    }

    public void setReceive_order_row_quantity(String receive_order_row_quantity) {
        this.receive_order_row_quantity = receive_order_row_quantity;
    }

    public String getReceive_order_row_unit_price() {
        return receive_order_row_unit_price;
    }

    public void setReceive_order_row_unit_price(String receive_order_row_unit_price) {
        this.receive_order_row_unit_price = receive_order_row_unit_price;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
