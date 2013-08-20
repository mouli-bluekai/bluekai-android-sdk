/*
 * Copyright 2013-present BlueKai, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bluekai.sdk.model;

import java.util.ArrayList;
import java.util.List;

public class ParamsList extends ArrayList<Params> {

	private static final long serialVersionUID = 6325567382289454690L;

	public ParamsList(){
		super();
	}

	public ParamsList(ParamsList paramsList) {
		super(paramsList);
	}
	
	public ParamsList(List<Params> paramsList) {
		super(paramsList);
	}

	public String getWhereClause() {
		StringBuilder whereClause = new StringBuilder(" where ");
		for (int i = 0; i < size(); i++) {
			Params params = get(i);
			whereClause.append("_id=" + params.getId());
			if (i + 1 < size()) {
				whereClause.append(" or ");
			}
		}
		return whereClause.toString();
	}
}
