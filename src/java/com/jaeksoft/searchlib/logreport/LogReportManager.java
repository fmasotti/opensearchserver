/**   
 * License Agreement for Jaeksoft OpenSearchServer
 *
 * Copyright (C) 2011 Emmanuel Keller / Jaeksoft
 * 
 * http://www.open-search-server.com
 * 
 * This file is part of Jaeksoft OpenSearchServer.
 *
 * Jaeksoft OpenSearchServer is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 * Jaeksoft OpenSearchServer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Jaeksoft OpenSearchServer. 
 *  If not, see <http://www.gnu.org/licenses/>.
 **/

package com.jaeksoft.searchlib.logreport;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.List;

import com.jaeksoft.searchlib.ClientCatalog;
import com.jaeksoft.searchlib.SearchLibException;
import com.jaeksoft.searchlib.request.SearchRequest;
import com.jaeksoft.searchlib.result.Result;
import com.jaeksoft.searchlib.util.Timer;

public class LogReportManager {

	final private DailyLogger logger;

	private SimpleDateFormat timeStampFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ssZ");

	public LogReportManager(String indexName) throws IOException {
		File dirLog = new File(ClientCatalog.OPENSEARCHSERVER_DATA, "logs");
		if (!dirLog.exists())
			dirLog.mkdir();
		logger = new DailyLogger(dirLog, "report." + indexName, timeStampFormat);
	}

	public void close() {
		logger.close();
	}

	final public void log(SearchRequest searchRequest, Timer timer,
			Result result) throws SearchLibException {
		if (searchRequest == null)
			return;
		if (!searchRequest.isLogReport())
			return;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append('\u0009');
			sb.append(URLEncoder.encode(searchRequest.getQueryString(), "UTF-8"));
			sb.append('\u0009');
			if (timer != null)
				sb.append(timer.duration());
			sb.append('\u0009');
			if (result != null)
				sb.append(result.getNumFound());
			sb.append('\u0009');
			sb.append(searchRequest.getStart());
			List<String> customLogs = searchRequest.getCustomLogs();
			if (customLogs != null) {
				for (String customLog : customLogs) {
					sb.append('\u0009');
					sb.append(URLEncoder.encode(customLog, "UTF-8"));
				}
			}
			logger.log(sb.toString());
		} catch (UnsupportedEncodingException e) {
			throw new SearchLibException(e);
		}
	}
}
