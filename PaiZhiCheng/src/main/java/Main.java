import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import top.flya.entity.Event;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        String baseUrl = "https://web.leesticket.com/api/ticket/index?page=";
        List<Integer> ticketList = getTicketList(baseUrl);
        String baseUrl2 = "https://web.leesticket.com/api/ticket/detail?id=";

        String temp = baseUrl2 + ticketList.get(0);
        System.out.println(temp);
        String body = HttpUtil.createPost(temp).body("{\n" +
            "    \"id\": "+ticketList.get(0)+"\n" +
            "}").execute().body();
        System.out.println(JSONUtil.toJsonPrettyStr(body));
        JSONObject jsonObject = new JSONObject(body);
        Event data = jsonObject.get("data", Event.class, false);
        System.out.println(data);
        System.out.println(JSONUtil.toJsonPrettyStr(data));

    }

    public static List<Integer> getTicketList(String baseUrl) {
        List<Integer> ids = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            String url = baseUrl + i + "&city_id=";
            System.out.println(url);
            String body = HttpUtil.createGet(url).execute().body();
            JSONObject jsonObject = new JSONObject(body);
            JSONArray data = jsonObject.getJSONObject("data").getJSONArray("list");
            data.toList(JSONObject.class).forEach(item -> {
                JSONObject json = new JSONObject(item);
                System.out.println(json.get("id"));
                ids.add((Integer) json.get("id"));
            });

        }
        return ids;
    }
}
