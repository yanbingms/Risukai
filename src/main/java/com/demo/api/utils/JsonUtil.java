package com.demo.api.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class JsonUtil {

    private static Gson gson = null;
    static {
		if (gson == null) {
		    gson = new Gson();
		}
    }

    private JsonUtil() {
    }

    /**
	 * 灏�瀵硅薄杞���㈡��json��煎��
	 *
	 * @param ts
	 * @return
	 */
    public static String objectToJson(Object ts) {
		String jsonStr = null;
		if (gson != null) {
		    jsonStr = gson.toJson(ts);
		}
		return jsonStr;
    }

    /**
	 * 灏�瀵硅薄杞���㈡��json��煎��(骞惰��瀹�涔���ユ����煎��)
	 *
	 * @param ts
	 * @return
	 */
    public static String objectToJsonDateSerializer(Object ts, final String dateformat) {
		String jsonStr = null;
		gson = new GsonBuilder().registerTypeHierarchyAdapter(Date.class,
				new JsonSerializer<Date>() {
				    @Override
					public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
					SimpleDateFormat format = new SimpleDateFormat(dateformat);
					return new JsonPrimitive(format.format(src));
				    }
				}).setDateFormat(dateformat).create();
		if (gson != null) {
		    jsonStr = gson.toJson(ts);
		}
		return jsonStr;
    }

    /**
	 * 灏�json��煎��杞���㈡��list瀵硅薄
	 *
	 * @param jsonStr
	 * @return
	 */
    public static List<?> jsonToList(String jsonStr) {
		List<?> objList = null;
		if (gson != null) {
		    Type type = new TypeToken<List<?>>() {
		    }.getType();
		    objList = gson.fromJson(jsonStr, type);
		}
		return objList;
    }

    /**
	 * 灏�json��煎��杞���㈡��map瀵硅薄
	 *
	 * @param jsonStr
	 * @return
	 */
    public static Map<String, Object> jsonToMap(String jsonStr) {
		Map<String, Object> objMap = null;
		if (gson != null) {
		    try {
				Type type = new TypeToken<Map<?, ?>>() {
				}.getType();
				objMap = gson.fromJson(jsonStr, type);
		    } catch (Exception e) {
		    	e.printStackTrace();
		    }
		}
		return objMap;
    }

    /**
	 * 灏�json杞���㈡��bean瀵硅薄
	 *
	 * @param jsonStr
	 * @return
	 */
    public static Object jsonToBean(String jsonStr, Class<?> cl) {
		Object obj = null;
		if (gson != null) {
		    obj = gson.fromJson(jsonStr, cl);
		}
		return obj;
    }

    /**
	 * 灏�json杞���㈡��bean瀵硅薄
	 *
	 * @param jsonStr
	 * @param cl
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public static <T> T jsonToBeanDateSerializer(String jsonStr, Class<T> cl, final String pattern) {
		Object obj = null;
		gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			    @Override
				public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
					SimpleDateFormat format = new SimpleDateFormat(pattern);
					String dateStr = json.getAsString();
					try {
					    return format.parse(dateStr);
					} catch (ParseException e) {
					    e.printStackTrace();
					}
					return null;
			    }
			}).setDateFormat(pattern).create();
		if (gson != null) {
		    obj = gson.fromJson(jsonStr, cl);
		}
		return (T) obj;
    }

    /**
	 * ��规��
	 *
	 * @param jsonStr
	 * @param key
	 * @return
	 */
    public static Object getJsonValue(String jsonStr, String key) {
		Object rulsObj = null;
		Map<?, ?> rulsMap = jsonToMap(jsonStr);
		if (rulsMap != null && rulsMap.size() > 0) {
		    rulsObj = rulsMap.get(key);
		}
		return rulsObj;
    }

    public static void addList(List<Map<String, Object>> listMessage, List<Map<String, Object>> listMessage2) {

    	if (listMessage != null && listMessage2 != null && listMessage2.size() > 0) {
    	    for (Map<String, Object> maps : listMessage2) {
    	    	listMessage.add(maps);
    	    }
    	}
    }

	public static int parseInt(Object o) {
		if(o == null) {
			return 0;
		}
		if(o instanceof Integer || o instanceof String) {
			return Integer.parseInt(o.toString());
		}
		return (int)(double)(Double) o;
	}

//	public static double parseDouble(Object o) {
//		if(o == null) {
//			return 0;
//		}
//		if(o instanceof Double) {
//			return (double) o;
//		}
//		if(o instanceof String) {
//			return Double.parseDouble(o.toString());
//		}
//		return (double) o;
//	}

	public static boolean getItemBoolean(Map<String, Object> map, String key) {
		if(map == null) {
			return false;
		}
		Object value = map.get(key);
		if(value != null && value instanceof Boolean) {
			return (Boolean) value;
		}
		return false;
	}

	public static String getItemString(Map<String, Object> map, String key) {
		if(map == null) {
			return "";
		}
		Object value = map.get(key);
		if(value != null && value instanceof Integer) {
			return (value) +"";
		}
		return value != null ? value+"" : "";
	}

	public static long getItemLong(Map<String, Object> map, String key) {
		if(map == null) {
			return 0;
		}
		try {
			Object o = map.get(key);
			if(o == null) {
				return 0;
			}
			if(o instanceof Long) {
				return Long.parseLong(o.toString());
			}
			if(o instanceof String) {
				if(((String) o).indexOf(".") > 0) {
					return Long.parseLong(o.toString().split(".")[0]);
				}
				return Long.parseLong(o.toString());
			}
			return (long)(double)(Double)o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static int getItemInt(Map<String, Object> map, String key) {
		if(map == null) {
			return 0;
		}
		try {
			Object o = map.get(key);
			if(o == null) {
				return 0;
			}
			if(o instanceof Integer || o instanceof String) {
				return Integer.parseInt(o.toString());
			}
			return (int)(double)(Double)o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getStarNum(String str) {
		DecimalFormat format = new DecimalFormat("0.#");
		String num = "0";
		try {
			num = format.format(Double.valueOf(str));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}
}
