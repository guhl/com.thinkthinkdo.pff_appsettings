/*
**    Copyright (C) 2012  Guhl
**
**    This program is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    This program is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.thinkthinkdo.pff_appsettings.content;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PFFSettingsContent {

    public static class PFFSettingsContentItem {

        public String id;
        public String content;

        public PFFSettingsContentItem(String id, String content) {
            this.id = id;
            this.content = content;
        }

        @Override
        public String toString() {
            return content;
        }
    }

    public static List<PFFSettingsContentItem> ITEMS = new ArrayList<PFFSettingsContentItem>();
    public static Map<String, PFFSettingsContentItem> ITEM_MAP = new HashMap<String, PFFSettingsContentItem>();

    static {
        addItem(new PFFSettingsContentItem("1", "READ_PHONE_STATE"));
        addItem(new PFFSettingsContentItem("2", "ACCESS_COARSE_LOCATION"));
        addItem(new PFFSettingsContentItem("3", "ACCESS_FINE_LOCATION"));
        addItem(new PFFSettingsContentItem("4", "READ_CONTACTS"));
        addItem(new PFFSettingsContentItem("5", "READ_CALL_LOG"));
        addItem(new PFFSettingsContentItem("6", "READ_CALENDAR"));
    }

    private static void addItem(PFFSettingsContentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }
}
