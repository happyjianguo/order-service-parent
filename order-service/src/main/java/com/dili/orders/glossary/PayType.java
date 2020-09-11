package com.dili.orders.glossary;

public enum PayType {
        CASH(1, "现金"),
        POS(2, "POS"),
        CARD(3, "刷卡"),
        THIRD_PARTY(4, "第三方"),
    	FREE(5, "免费");

        private PayType(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        private int code;
        private String desc;

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public static String getPayType(int code) {
            for (PayType r : PayType.values()) {
                if (r.getCode() == code) {
                    return r.getDesc();
                }
            }
            return "";
        }
    }
