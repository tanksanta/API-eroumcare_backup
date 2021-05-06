package kr.co.thkc.dispatch;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class BaseRequest {

	public static final String TYPE_INTEGER = "Integer";
	public static final String TYPE_STRING  = "String";
	public static final String TYPE_MAP     = "LinkedHashMap";
	public static final String TYPE_LIST    = "ArrayList";
	public static final String TYPE_BOOLEAN    = "Boolean";


	private Map<String, Object> params;

	private List<String> requiredField = new ArrayList<String>();
	private Map<String, String> requiredFieldType = new HashMap<String, String>();

	public BaseRequest(){}
	public BaseRequest(Map<String, Object> request){
		this.params = request;
	}


	/*
	* getRequest 
	*/
	public Map<String, Object> bindRequest() throws Exception {
		if ( this.params == null) throw new Exception("getRequest Fail. Request is null");
		Map<String,Object> returnMap = this.params;
		for(Map.Entry<String,Object> entry :returnMap.entrySet()){
			entry.setValue(convertValueFromEscape(entry.getValue()));
		}
		return returnMap;
	}

	private Object convertValueFromEscape(Object from){
		if(from instanceof String){
			from = StringEscapeUtils.unescapeHtml4(from.toString());
		}else if(from instanceof Map){
			Map<String,Object> map = (Map<String,Object>)from;
			for(Map.Entry<String,Object> entry :map.entrySet()){
				entry.setValue(convertValueFromEscape(entry.getValue()));
			}
			from = map;
		}else if(from instanceof List){
			List list= (List)from;
			for(Object item :list){
				list.set(list.indexOf(item), convertValueFromEscape(item));
			}
			from = list;
		}
		return from;
	}

	/*
	* 필수컬럼 조회
	*/
	public String getRequriedField() {
		String returnValue = "requiredField : ";
		if ( this.requiredField.size() > 0) {
			for(String keyName : this.requiredField) {
				returnValue += String.format("[%s] ", keyName);
			}
		}
		return returnValue;
	}

	/*
	* 필수컬럼 추가
	*/
	public void addRequiredField(String field, String type) {
		if(!field.isEmpty() && !this.requiredField.contains(field)  )   {
			this.requiredField.add(field);
			this.requiredFieldType.put(field, type);
		}
	}


	/*
	* 필수컬럼 목록 선택삭제
	*/
	public void removeRequiredField(String field) {
		if( this.requiredField.contains(field) ){
			this.requiredField.remove(field);
			this.requiredFieldType.remove(field);
		}
	}

	/*
	* 필수컬럼 목록 전체삭제
	*/
	public void clearRequiredField() {
		this.requiredField.clear();
		this.requiredFieldType.clear();
	}

	/*
	* 필수컬럼 체크
	*/
	public void validRequiredField() throws Exception {
		String errMessage = null;

		if (this.requiredField.size() > 0) {
			for(String keyName : this.requiredField) {
				if( !this.params.containsKey(keyName)) {
					// 해당 키 없다.
					errMessage = String.format("requiredField [%s] is not contain ", keyName);
					// 오류 발생
					break;
				}

				if (this.params.get(keyName) == null || this.params.get(keyName).toString().isEmpty()) {
					// 해당값 null 또는 빈값
					errMessage = String.format("requiredField [%s] is null ", keyName);
					break;
				}

				String keyType = (this.params.get(keyName).getClass().getSimpleName() );
				String fieldType = this.requiredFieldType.get(keyName);
				if( !keyType.equals(fieldType)) {
					errMessage = String.format("requiredField [%s (%s)] is wrong. requested type -> %s ", keyName, fieldType, keyType);
					break;
				}

//				if( fieldType.equals(TYPE_LIST) &&  ((ArrayList)this.params.get(keyName)).size() == 0  ) {
//					// ArrayList 인 경우, 값이 0개이면 오류
//					errMessage = String.format("requiredFiled[%s (%s)] size is 0", keyName, keyType);
//					break;
//				}

			}
		}

		if(errMessage != null)
			throw new Exception(errMessage);
	}
}
