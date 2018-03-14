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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.RemoteException;
//import java.util.Base64;
import java.util.List;

//import org.apache.axis.message.MessageElement;
//import org.apache.axis.types.Schema;
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
//		Base64.Decoder decoder = Base64.getDecoder();
//		Base64.Encoder encoder = Base64.getEncoder();
//		String encodedText = encoder.encodeToString(result);
//		System.out.println(encodedText);
//		//解码
//		try {
//			System.out.println(new String(decoder.decode(encodedText), "UTF-8"));
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//        Schema schema = (Schema)result;
//        MessageElement[] msgele = schema.get_any();
//        List FOCElementHead = msgele[0].getChildren();//消息头,DataSet对象
//        List FOCElementBody = msgele[1].getChildren();//消息体信息,DataSet对象
//       
//        if (FOCElementBody.size() <= 0){
//         System.out.println("无消息体");
//        }
//       
//        String nn = FOCElementBody.get(0).toString();//消息体的字符串形式
//        try {
//            saveXMLString(nn,"c://test.xml");//保存为XML形式
////            this.parserXml("c://test.xml");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
		System.out.println(result);
	}
	
	@SuppressWarnings("unused")
	 private static void saveXMLString(String XMLString, String fileName)throws IOException {   
	        File file = new File(fileName);   
	        if (file.exists()) {   
	            file.delete();   
	        }   
	        file.createNewFile();   
	        if (file.canWrite()) {   
	            FileWriter fileOut = new FileWriter(file);   
	            fileOut.write(XMLString);   
	            fileOut.close();   
	        }   
	 }
	
}
