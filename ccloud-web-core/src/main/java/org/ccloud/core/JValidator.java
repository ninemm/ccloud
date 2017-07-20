/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (ninemm@qq.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ccloud.core;

import com.jfinal.validate.Validator;

public abstract class JValidator extends Validator {

	/** 校验手机号码正则表达式  */
	private static final String PATTERN_MOBILE = "\\b(1[3,5,7,8]{1}\\d{9})\\b";
	/** 校验电话号码正则表达式  */
	private static final String PATTERN_PHONE = "\\b(0(\\d{2,3})[-]{0,1}\\d{6,9})\\b";
	/** QQ正则表达式  */
	private static final String PATTERN_QQ = "\\b([1-9]\\d{4,})\\b"; 
	
	protected void validatePatternMobile(String field, String errorKey, String errorMessage) {
		validatePatternMobile(field, false, errorKey, errorMessage);
	}
	
	protected void validatePatternMobile(String field, boolean isCaseSensitive, String errorKey, String errorMessage) {
		validateRegex(field, PATTERN_MOBILE, isCaseSensitive, errorKey, errorMessage);
	}
	
	protected void validatePatternPhone(String field, String errorKey, String errorMessage) {
		validatePatternPhone(field, false, errorKey, errorMessage);
	}
	
	protected void validatePatternPhone(String field, boolean isCaseSensitive, String errorKey, String errorMessage) {
		validateRegex(field, PATTERN_PHONE, isCaseSensitive, errorKey, errorMessage);
	}
	
	protected void validatePatternQQ(String field, String errorKey, String errorMessage) {
		validatePatternQQ(field, false, errorKey, errorMessage);
	}
	
	protected void validatePatternQQ(String field, boolean isCaseSensitive, String errorKey, String errorMessage) {
		validateRegex(field, PATTERN_QQ, isCaseSensitive, errorKey, errorMessage);
	}
}
