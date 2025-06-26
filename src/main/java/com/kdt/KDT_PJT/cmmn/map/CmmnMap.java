package com.kdt.KDT_PJT.cmmn.map;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.kdt.KDT_PJT.cmmn.util.common.CmmnUtil;

public class CmmnMap extends LinkedHashMap<Object, Object>{

	private static final long serialVersionUID = -4485149791269586840L;

	
	
	@Override
	public CmmnMap put(Object key, Object value) {

		super.put(key, value);
		
		return this;
	}

	@Override
	public Object get(Object key) {
		return super.get(key);
	}
	
	public String getString(Object key) {
		Object val = get(key);
		if("Integer".equals(CmmnUtil.whatIs(val))) {
			return Integer.toString((int)val);
		} else {
			return (String)val;
		}
	}
	
	public String getString(Object key, String deflt) {
		Object val = get(key);
		if("Integer".equals(CmmnUtil.whatIs(val))) {
			return Integer.toString((int)val);
		} else if("String".equals(CmmnUtil.whatIs(val))) {
			return (String)val;
		}
		return val == null ? deflt : (String)val;
	}
	
	public Boolean getBoolean(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.parseBoolean(value.toString().trim());
		}
	}
	
	public Boolean getBoolean(Object key, boolean deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof Boolean) {
			return (Boolean) value;
		} else {
			return Boolean.parseBoolean(value.toString().trim());
		}
	}
	
	public Integer getInt(Object key) {
		return getInteger(key);
	}
	
	public Integer getInt(Object key, int deflt) {
		return getInteger(key, deflt);
	}
	
	public Integer getInteger(Object key, int deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else if (value.toString().trim().equals("")) {
			return deflt;
		} else {
			return Integer.valueOf(value.toString().trim());
		}
	}
	
	public Integer getInteger(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Integer) {
			return (Integer) value;
		} else {
			return Integer.valueOf(value.toString().trim());
		}
	}
	
	public Long getLong(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Long) {
			return (Long) value;
		} else {
			return Long.valueOf(value.toString().trim());
		}
	}

	public Float getFloat(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Float) {
			return (Float) value;
		} else {
			return Float.valueOf(value.toString().trim());
		}
	}

	public Double getDouble(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Double) {
			return (Double) value;
		} else {
			return Double.valueOf(value.toString().trim());
		}
	}

	public BigDecimal getBigDecimal(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof BigDecimal) {
			return (BigDecimal) value;
		} else {
			return new BigDecimal(value.toString().trim());
		}
	}
	
	@SuppressWarnings("rawtypes")
	public CmmnMap getCmmnMap(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof Map) {
			try {
				return (CmmnMap)value;
			} catch(ClassCastException e) {
				Map map = (Map)value;
				Set mapkeys = map.keySet();
				CmmnMap rslt = new CmmnMap();
				for(Object mapkey : mapkeys) {
					rslt.put(mapkey, map.get(mapkey));
				}
				put(key,rslt);
				
				return rslt;
			}
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public CmmnMap getCmmnMap(Object key, CmmnMap deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof Map) {
			try {
				return (CmmnMap)value;
			} catch(ClassCastException e) {
				Map map = (Map)value;
				Set mapkeys = map.keySet();
				CmmnMap rslt = new CmmnMap();
				for(Object mapkey : mapkeys) {
					rslt.put(mapkey, map.get(mapkey));
				}
				put(key,rslt);
				
				return rslt;
			}
		} else {
			return deflt;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<CmmnMap> getCmmnMapList(Object key) {
		Object value = get(key);
		if (value == null) {
			return new ArrayList<>();
		} else if (value instanceof List) {
			List list = (List)value;
			if(list.size()>0) {
				Map map = (Map) list.get(0);
				Set mapkeys;
				List<CmmnMap> rslt = new ArrayList<>();
				for(Object obj : list) {
					map = (Map)obj;
					mapkeys = map.keySet();
					CmmnMap cmmnMap = new CmmnMap();
					for(Object mapkey : mapkeys) {
						cmmnMap.put(mapkey, map.get(mapkey));
					}
					rslt.add(cmmnMap);
				}
				put(key, rslt);
				return rslt;
			} else {
				return new ArrayList<CmmnMap>();
			}
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public List<CmmnMap> getCmmnMapList(Object key, ArrayList<CmmnMap> deflt) {
		Object value = get(key);
		if (value == null) {
			return deflt;
		} else if (value instanceof List) {
			List list = (List)value;
			if(list.size()>0) {
				Map map = (Map) list.get(0);
				try {
					CmmnMap tmp = (CmmnMap) map;
					return (List<CmmnMap>)list;
				} catch(ClassCastException e) {
					Set mapkeys;
					List<CmmnMap> rslt = new ArrayList<>();
					for(Object obj : list) {
						map = (Map)obj;
						mapkeys = map.keySet();
						CmmnMap cmmnMap = new CmmnMap();
						for(Object mapkey : mapkeys) {
							cmmnMap.put(mapkey, map.get(mapkey));
						}
						rslt.add(cmmnMap);
					}
					put(key, rslt);
					return rslt;
				}
			} else {
				return new ArrayList<CmmnMap>();
			}
		} else {
			return deflt;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public HashMap getHashMap(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof HashMap) {
			return (HashMap) value;
		} else {
			return null;
		}
	}

	public Date getDate(Object key) {
		return getDateFormat(key, "yyyy-MM-dd");
	}

	public Date getDateTime(Object key) {
		return getDateFormat(key, "yyyy-MM-dd HH:mm:ss");
	}

	public Date getDateFormat(Object key, String format) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) value;
			return new Date(sqlDate.getTime());
		} else if (value instanceof java.util.Date) {
			return (Date) value;
		} else {
			String str = value.toString().trim();
			SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.KOREA);
			sdf.setLenient(false);
			try {
				return sdf.parse(str);
			} catch (ParseException e) {
				return null;
			}
		}
	}

	public Timestamp getTimestamp(Object key) {
		Object value = get(key);
		if (value == null) {
			return null;
		} else if (value instanceof java.sql.Date) {
			java.sql.Date sqlDate = (java.sql.Date) value;
			return new Timestamp(sqlDate.getTime());
		} else if (value instanceof Timestamp) {
			return (Timestamp) value;
		} else if (value instanceof Date) {
			Date date = (Date) value;
			return new Timestamp(date.getTime());
		} else {
			return Timestamp.valueOf(value.toString());
		}
	}

	@Override
	public String toString() {
		
		Set<Object> keys = this.keySet();
		StringBuilder sb = new StringBuilder().append("[");
		Object value;
		for(Object key : keys) {
			if(key instanceof BigDecimal) {
				sb.append(((BigDecimal)key).toPlainString());
			} else {
				sb.append(key);
			}
			
			sb.append("=");
			
			value = this.get(key);
			
			if(value instanceof BigDecimal) {
				sb.append(((BigDecimal)value).toPlainString());
			} else {
				sb.append(String.valueOf(value));
			}
			
			sb.append(", ");
		}
		if(sb.length() > 3) {
			sb.deleteCharAt(sb.length()-1);
			sb.deleteCharAt(sb.length()-1);
		}
		
		sb.append("]");
		return sb.toString();
	}
	
	
}
