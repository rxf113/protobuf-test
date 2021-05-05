package com.rxf113.protocol;

import com.rxf113.MsgProtos;
import org.junit.Test;

import java.io.*;

public class TestProto {

    public static MsgProtos.Msg builderMsg() {
        MsgProtos.Msg.Builder builder = MsgProtos.Msg.newBuilder();
        builder.setType(10086);
        builder.setContent("你好rxf113");

        MsgProtos.Msg build = builder.build();
        return build;
    }

    static class Msg implements Serializable{
        private int id;
        private String content;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    @Test
    public void serAndDeser1() throws IOException {
        MsgProtos.Msg msg = builderMsg();
        byte[] bytes = msg.toByteArray();

        Msg msg2 = new Msg();
        msg2.setId(10080);
        msg2.setContent("你好rxf113");

        String tt = "F:\\gitee\\protobuf\\src\\main\\java\\com\\rxf113\\tt";
        FileOutputStream outputStream = new FileOutputStream(tt);
        ObjectOutputStream objectOutputStream =
                new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(msg2);
        outputStream.close();
        objectOutputStream.close();

       InputStream inputStream = new FileInputStream(tt);
        int available = inputStream.available();
        System.out.println(available);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//        outputStream.write(bytes);
//        bytes = outputStream.toByteArray();

        MsgProtos.Msg msg1 = MsgProtos.Msg.parseFrom(bytes);
        System.out.println("msg1 : type = " + msg1.getType());
        System.out.println("msg1 : content = " + msg1.getContent());
    }
}
