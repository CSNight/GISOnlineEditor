package com.supermap.spscsi.dataop;

import com.supermap.data.FieldType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataInfoBuliding {
    public List<String[]> parseCoordinate(int field, Map<Integer, Object> map) {
        List<String[]> xys = new ArrayList<String[]>();

        String line = map.get(field).toString().replace(";", ",");
        String[] xy = line.split(",");
        for (int j = 0; j < xy.length; j = j + 2) {
            xys.add(new String[]{xy[j], xy[j + 1]});
        }
        return xys;
    }

    public List<String[]> parseCoordinatepoi(int fieldx, int fieldy,
                                             Map<Integer, Object> map) {
        List<String[]> xys = new ArrayList<String[]>();
        xys.add(new String[]{map.get(fieldx).toString(),
                map.get(fieldy).toString()});
        return xys;
    }

    public List<Map<String, Object>> parsefieldinfo(String[] title) {
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 0; i < title.length; i++) {
            Map<String, Object> map = new HashMap<>();
            if (title[i] != "POST_ORG_AREA" || title[i] != "POST_SECTION_AREA")
                map.put("fieldname", title[i].replace(" ", ""));
            map.put("fieldtype", FieldType.WTEXT);
            map.put("fieldlength", 200);
            list.add(map);
        }

        return list;
    }
}
