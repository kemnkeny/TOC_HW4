/*
 
 林彥旭
F40010222
用nputStream 和 BufferedReader 讀取 URL 的內容
存入JSON Array後，用for loop逐一搜尋
用RE把XX大道、XX路、XX街找出，都沒有才找巷 
由map來儲存
map的key值均為道路名
最後將結果印出

*/

import java.net.*;
import java.io.*;
import org.json.*;
import java.lang.String;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class TocHw4 
{
	public static void main(String[] args) throws Exception
	{
		try
		{
			if(args.length == 1)
			{
				String url = args[0];
				String[] value =  new String[3];
				Map<String, Map<Integer, Integer>> road_date;
				Map<String, Integer> maxPrice;
				Map<String, Integer> minPrice;
				Map<String, Integer> dataCount;
				
				//System.out.println("GO");
				//String url = "http://www.datagarage.io/api/538447a07122e8a77dfe2d86";
				
				road_date = new HashMap<String, Map<Integer, Integer>>();
				maxPrice = new HashMap<String, Integer>();
				minPrice = new HashMap<String, Integer>();
				dataCount = new HashMap<String, Integer>();
				
				Pattern pattern1 = Pattern.compile(".*大道|.*路|.*街");
				Pattern pattern2 = Pattern.compile(".*巷");

				InputStream urlFile = new URL(url).openStream();
				BufferedReader rd = new BufferedReader(new InputStreamReader(urlFile, Charset.forName("UTF-8")));
				JSONArray jsonData = new JSONArray(new JSONTokener(rd));
				JSONObject jsonObj;
				
				for(int i = 0; i < jsonData.length(); i++)
				{
					jsonObj = jsonData.getJSONObject(i);
					
					Matcher matcher1 = pattern1.matcher(jsonObj.getString("土地區段位置或建物區門牌"));
					Matcher matcher2 = pattern2.matcher(jsonObj.getString("土地區段位置或建物區門牌"));
					
					
					if(matcher1.find())
					{
						String tmpRoad = matcher1.group();
						int tmpYear = jsonObj.getInt("交易年月");
						int tmpPrice = jsonObj.getInt("總價元");
						
						// 初次發現這條道路
						if(dataCount.get(tmpRoad) == null)
						{
							road_date.put(tmpRoad, new HashMap<Integer, Integer>());
							dataCount.put(tmpRoad, 1);
							road_date.get(tmpRoad).put(tmpYear, 1);
							maxPrice.put(tmpRoad, tmpPrice);
							minPrice.put(tmpRoad, tmpPrice);
						}
						else
						{
							//初次發現道路與年月組合
							if(road_date.get(tmpRoad).get(tmpYear) == null)
							{
								road_date.get(tmpRoad).put(tmpYear, 1);
								dataCount.put(tmpRoad, dataCount.get(tmpRoad)+1);
							}
							if(maxPrice.get(tmpRoad) < tmpPrice)
									maxPrice.put(tmpRoad, tmpPrice);
							if(minPrice.get(tmpRoad) > tmpPrice)
								minPrice.put(tmpRoad, tmpPrice);
						}
					}
					else if(matcher2.find())	// 都沒找到才找"巷"
					{
						String tmpRoad = matcher2.group();
						Integer tmpYear = jsonObj.getInt("交易年月");
						int tmpPrice = jsonObj.getInt("總價元");
						
						// 初次發現這條道路
						if(dataCount.get(tmpRoad) == null)
						{
							road_date.put(tmpRoad, new HashMap<Integer, Integer>());
							dataCount.put(tmpRoad, 1);
							road_date.get(tmpRoad).put(tmpYear, 1);
							maxPrice.put(tmpRoad, tmpPrice);
							minPrice.put(tmpRoad, tmpPrice);
						}
						else
						{
							//初次發現道路與年月組合
							if(road_date.get(tmpRoad).get(tmpYear) == null)
							{
								road_date.get(tmpRoad).put(tmpYear, 1);
								dataCount.put(tmpRoad, dataCount.get(tmpRoad)+1);
							}
							if(maxPrice.get(tmpRoad) < tmpPrice)
								maxPrice.put(tmpRoad, tmpPrice);
							if(minPrice.get(tmpRoad) > tmpPrice)
								minPrice.put(tmpRoad, tmpPrice);
						}
					}
				}
				int max, min;
				max = min = 0;
				Iterator iter = dataCount.entrySet().iterator();
				while(iter.hasNext()) // 先找出最大data數
				{
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if((int) val > max)
						max = (int) val;
				}
				iter = dataCount.entrySet().iterator();
				while(iter.hasNext()) // 再次比對
				{
					Map.Entry entry = (Map.Entry) iter.next();
					Object key = entry.getKey();
					Object val = entry.getValue();
					if((int) val >= max)
					{
						System.out.print(key + ", ");
						System.out.println("最高成交價：" + maxPrice.get(key) + ", 最低成交價：" + minPrice.get(key));
					}
				}
			} // end if (args)
		} // end try
		catch(Exception e)
		{
			System.out.println("File not found!");
		}
	}
}

