package com.demo.api.handle;

import com.demo.api.entity.EEntity;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 *
 * Created by mhh on 2017/3/20.
 */
public class EntityData {

    private int id = 1;

    private List<EEntity> list;

    public void setData(List<EEntity> list) {
        this.list = list;
    }

    private int getSize() {
        return list != null ? list.size() : 0;
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(list);
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        if(list != null && list.size() > 0) {
            buffer.append("F,A7354101,"+list.get(0).getSId()+","+new SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss'Z'").format(new Date())+",IedF01Rq,4,1");
            buffer.append("\r");
            for (EEntity e : list) {
                buffer.append(e.toString());
                buffer.append("\r");
            }
            buffer.append("E,"+getSize()+","+getSize()+","+(getSize()*2+2));
            buffer.append("\r");
        }
        return buffer.toString();
    }
}
