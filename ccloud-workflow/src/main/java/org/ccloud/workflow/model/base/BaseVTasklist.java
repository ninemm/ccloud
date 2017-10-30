/**
 * Copyright (c) 2015-2016, Eric Huang 黄鑫 (hx50859042@gmail.com).
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

package org.ccloud.workflow.model.base;

import org.ccloud.model.core.JModel;

import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by 九毫米(http://9mm.tech).
 */
@SuppressWarnings("serial")
public abstract class BaseVTasklist<M extends BaseVTasklist<M>> extends JModel<M> implements IBean {

	public void setTASKID(java.lang.String TASKID) {
		set("TASKID", TASKID);
	}

	public java.lang.String getTASKID() {
		return get("TASKID");
	}

	public void setINSID(java.lang.String INSID) {
		set("INSID", INSID);
	}

	public java.lang.String getINSID() {
		return get("INSID");
	}

	public void setTASKDEFKEY(java.lang.String TASKDEFKEY) {
		set("TASKDEFKEY", TASKDEFKEY);
	}

	public java.lang.String getTASKDEFKEY() {
		return get("TASKDEFKEY");
	}

	public void setDEFKEY(java.lang.String DEFKEY) {
		set("DEFKEY", DEFKEY);
	}

	public java.lang.String getDEFKEY() {
		return get("DEFKEY");
	}

	public void setDEFNAME(java.lang.String DEFNAME) {
		set("DEFNAME", DEFNAME);
	}

	public java.lang.String getDEFNAME() {
		return get("DEFNAME");
	}

	public void setTASKNAME(java.lang.String TASKNAME) {
		set("TASKNAME", TASKNAME);
	}

	public java.lang.String getTASKNAME() {
		return get("TASKNAME");
	}

	public void setASSIGNEE(java.lang.String ASSIGNEE) {
		set("ASSIGNEE", ASSIGNEE);
	}

	public java.lang.String getASSIGNEE() {
		return get("ASSIGNEE");
	}

	public void setCANDIDATE(java.lang.String CANDIDATE) {
		set("CANDIDATE", CANDIDATE);
	}

	public java.lang.String getCANDIDATE() {
		return get("CANDIDATE");
	}

	public void setDEFID(java.lang.String DEFID) {
		set("DEFID", DEFID);
	}

	public java.lang.String getDEFID() {
		return get("DEFID");
	}

	public void setDELEGATIONID(java.lang.String DELEGATIONID) {
		set("DELEGATIONID", DELEGATIONID);
	}

	public java.lang.String getDELEGATIONID() {
		return get("DELEGATIONID");
	}

	public void setDESCRIPTION(java.lang.String DESCRIPTION) {
		set("DESCRIPTION", DESCRIPTION);
	}

	public java.lang.String getDESCRIPTION() {
		return get("DESCRIPTION");
	}

	public void setCREATETIME(java.lang.String CREATETIME) {
		set("CREATETIME", CREATETIME);
	}

	public java.lang.String getCREATETIME() {
		return get("CREATETIME");
	}

	public void setDUEDATE(java.lang.String DUEDATE) {
		set("DUEDATE", DUEDATE);
	}

	public java.lang.String getDUEDATE() {
		return get("DUEDATE");
	}

}
