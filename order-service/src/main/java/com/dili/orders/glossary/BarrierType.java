package com.dili.orders.glossary;

public enum BarrierType{
    	
    	LOADED(1,"重车进门"),
    	EMPTY(2,"空车进门"),
		ZLC(3,"转离场");
    	
    	private int code;
    	private String desc;
    	
		private BarrierType(int code, String desc) {
			this.code = code;
			this.desc = desc;
		}
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
		
		public static String getBarrierType(int code){
			return make(code).desc;
		}
		
		public static BarrierType make(int code){
			for(BarrierType t : BarrierType.values()){
				if(t.code == code){
					return t;
				}
			}		
			throw new IllegalArgumentException("no barrierType match with code【"+code+"】");
		}
    }