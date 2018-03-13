/**
 * Copyright (c) 2015-2016, 九毫米(Eric Huang) (hx50859042@gmail.com).
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
package org.ccloud.mid;

import java.io.UnsupportedEncodingException;
import java.rmi.RemoteException;
//import java.util.Base64;
import java.util.List;

import org.ccloud.middledb.ArrayOfAnyType;
import org.ccloud.middledb.ArrayOfQYBasicFlowType;
import org.ccloud.middledb.MiddleWebService;
import org.ccloud.middledb.MiddleWebServiceSoap;
import org.ccloud.middledb.ObjectFactory;
import org.ccloud.middledb.QYBasicFlowType;

import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

public class MidDataUtil {
	
	private static String userName = PropKit.use("midData.properties").get("middata_soap_username");
	private static String passWord = PropKit.use("midData.properties").get("middata_soap_password");
	private static MiddleWebService middleWebService = new MiddleWebService();
	private static MiddleWebServiceSoap middleWebServiceSoap = middleWebService.getMiddleWebServiceSoap();	

	public static void main(String[] args) throws RemoteException {
//		 // 创建一个MiddleWS工厂
//		 MiddleWebService factory = new MiddleWebService();
//		 // 根据工厂创建一个MiddleWSSoap对象
//		 MiddleWebServiceSoap middleWSSoap = factory.getMiddleWebServiceSoap();
//		 // 调用WebService提供的getQYBasicFeeTypeFromMidDB方法获取分类基础信息
//		 ArrayOfAnyType anyType = new ArrayOfAnyType();
//		 byte[] x = middleWSSoap.getQYBasicFeeTypeFromMidDB(anyType);

		// MiddleWebServiceLocator Locator = null;
		// MiddleWebServiceSoapStub stub = null;
		// try {
		// // 先new一个xxLocator对象
		// Locator = new MiddleWebServiceLocator();
		// // 调用xxLocator对象的getXXPort()方法生成xxBindingStub对象
		// stub = (MiddleWebServiceSoapStub)
		// Locator.getPort(MiddleWebServiceSoap.class);
		// } catch (javax.xml.rpc.ServiceException e) {
		// e.printStackTrace();
		// }
		//
		// byte[] result = stub.getQY_BasicFeeTypeFromMidDB(null);
//		MiddleWebService webservice = new MiddleWebService();
//		MiddleWebServiceSoap middleWebServiceSoap = webservice.getMiddleWebServiceSoap();
//		byte[] x = middleWebServiceSoap.getQYActivityFromMidDB(null);
//		if (x != null) {
//			Base64.Decoder decoder = Base64.getDecoder();
//			Base64.Encoder encoder = Base64.getEncoder();
//			String encodedText = encoder.encodeToString(x);
//			System.out.println(encodedText);
//			//解码
//			try {
//				System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
		MidDataUtil.getActivityInfo("2018-03-02", "2018-03-12", "1", "10");
	}

	//写入数据接口示例
	public static int Syn() {
		MiddleWebService middleWebService = new MiddleWebService();
		MiddleWebServiceSoap middleWebServiceSoap = middleWebService.getMiddleWebServiceSoap();	
	 
		ArrayOfQYBasicFlowType flowTypeList = new ArrayOfQYBasicFlowType();		
		List<QYBasicFlowType> qyFlowTypeList = flowTypeList.getQYBasicFlowType();

		ObjectFactory objectFactory=new ObjectFactory();
		QYBasicFlowType qyFlowType = objectFactory.createQYBasicFlowType();
		qyFlowType.setFlowTypeID(StrKit.getRandomUUID());
		qyFlowType.setFlowTypeName("测试终端流程");
		qyFlowType.setParentID("");
		qyFlowType.setMemo("测试数据");
        qyFlowType.setCreateTime("2018-3-12 14:25:00");
        qyFlowType.setModifyTime("2018-3-12 14:25:00");
        qyFlowType.setFlag(1);
        
		qyFlowTypeList.add(qyFlowType);	
		int account = middleWebServiceSoap.syncQYBasicFlowTypeToMidDB(userName, passWord, flowTypeList);
		return account;
	}

	//读取数据接口示例
	public static void getActivityInfo(String startDate, String endDate, String pageNum, String pageCount) {
		ArrayOfAnyType anyType = new ArrayOfAnyType();
		List<Object> param = anyType.getAnyType();
		param.add(0, startDate);
		param.add(1, endDate);
		param.add(2, pageNum);
		param.add(3, pageCount);
		byte[] result = middleWebServiceSoap.getQYActivityFromMidDB(anyType);
		System.out.println(result);
	}
	
}
