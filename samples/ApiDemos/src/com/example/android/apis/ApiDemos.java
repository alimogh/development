/*
 * KJK_TALK APIDEMOS: Entry Point 

 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.apis;

import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiDemos extends ListActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent intent = getIntent();
        //�Ʒ� browseIntent���� �־��� ���� ������ �о�ͼ� 
        String path = intent.getStringExtra("com.example.android.apis.Path");
        
        if (path == null) {
            path = "";
        }


        //SimpleAdapter�� 2nd param���� map<string,?>�� item���� ���� list�� ���ڷ� �޴´�.
        setListAdapter(new SimpleAdapter(this, getData(path),
                android.R.layout.simple_list_item_1, new String[] { "title" },
                new int[] { android.R.id.text1 }));
        getListView().setTextFilterEnabled(true);
    }

    //���� dir�� �������� list�� ��µ� data�� �� data�� ȣ���� intent�� �����ϴ� ��Ȱ�� �Ѵ�.
    protected List<Map<String, Object>> getData(String prefix) {
        //KJK_TALK: Map(key, value)�� item���� ������ ArrayList���� 
        List<Map<String, Object>> myData = new ArrayList<Map<String, Object>>();

        //intent�� �ϳ� �����, apidemos�� ���� sample category�� ��������
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_SAMPLE_CODE);

        PackageManager pm = getPackageManager();
        //������ ������� intent�� ȣ��ɼ� �ִ� ��� activity list�� ���� 
        List<ResolveInfo> list = pm.queryIntentActivities(mainIntent, 0);

        if (null == list)
            return myData;

        String[] prefixPath;
        
        if (prefix.equals("")) {
            prefixPath = null;//��ó�� getData call��
        } else {
            prefixPath = prefix.split("/"); //���� dir �Ʒ������� list�� �����ϵ��� �Ѵ�.
        }

        //���� activity list�� item ������ ����, ���� 199�� 
        int len = list.size();

        //<string, boolean>�� item���� ���� HashMap ����
        Map<String, Boolean> entries = new HashMap<String, Boolean>();

        for (int i = 0; i < len; i++) {
            //list�� ù��° act�� �����ͼ� info�� ����.
            ResolveInfo info = list.get(i);
            CharSequence labelSeq = info.loadLabel(pm);
            //title �̸��� ���ϰ�.
            String label = labelSeq != null
                    ? labelSeq.toString()
                    : info.activityInfo.name;
            
            if (prefix.length() == 0 || label.startsWith(prefix)) {
                //App/Activity/Hello World
                String[] labelPath = label.split("/");
                //App, Activity, Hello World
                String nextLabel = prefixPath == null ? labelPath[0] : labelPath[prefixPath.length];

                if ((prefixPath != null ? prefixPath.length : 0) == labelPath.length - 1) {
                //������ leaf node�ΰ��, �� act�� ȣ���ϴ� ��� 
                String className = info.activityInfo.name;	
                    addItem(myData, nextLabel + className.substring(className.lastIndexOf(".")) , activityIntent(
                            info.activityInfo.applicationInfo.packageName,
                            info.activityInfo.name));
                } else {
                    if (entries.get(nextLabel) == null) {
                        //App�� ���� ó���� ���� dir�� ���
                        //App, Media, OS, Contents, Graphics, Text, View .. ���� ù��° dir�� 
                        //�װ��� sub Menu Act�� ȣ���Ҽ� �ִ� intent�� ���� myData�� ���� �ִ´�. 
                        //�̶� enries���� ����Ͽ� �ش� path�� �̹� ��ϵȰ����� �Ǵ��ϴµ� ����Ѵ�.

                        //nextLable:
                        addItem(myData, nextLabel, browseIntent(prefix.equals("") ? nextLabel : prefix + "/" + nextLabel));
                        entries.put(nextLabel, true);
                    }
                }
            }
        }

        //Collection�� sub class�� list myData�� ordering�Ѵ�.
        Collections.sort(myData, sDisplayNameComparator);
        
        return myData;
    }

    private final static Comparator<Map<String, Object>> sDisplayNameComparator =
        new Comparator<Map<String, Object>>() {
        private final Collator   collator = Collator.getInstance();

        //HashMap->HashMap$Entry.<String, Object>�������� HashMap->HashMap$Entry.<title, dir>
        //�� ������ ������ �õ��Ѵ�. compare�� �ᱹ ���ڿ��� ���Ѵ�.
        public int compare(Map<String, Object> map1, Map<String, Object> map2) {
/*            StringBuffer s1 = new StringBuffer(map1.get("title").toString());
            s1.reverse();
            StringBuffer s2 = new StringBuffer(map2.get("title").toString());
            s2.reverse();
        
            return collator.compare(s2.toString(), s1.toString()); */
            return collator.compare(map1.get("title"), map2.get("title"));
        }
    };

    //leaf node�ΰ�� ������ ȣ���� act������ ����Ѵ�.
    protected Intent activityIntent(String pkg, String componentName) {
        Intent result = new Intent();
        result.setClassName(pkg, componentName);
        return result;
    }

    //leaf node�� �ƴѰ�� ApiDemos�� �̿��Ͽ� list�� ����ϴµ�,�������� dir�� �Ѱ�
    //list�� ��� ���� ������� �˷��ش�.
    protected Intent browseIntent(String path) {
        Intent result = new Intent();
        result.setClass(this, ApiDemos.class);
        result.putExtra("com.example.android.apis.Path", path);
        return result;
    }

    // Map(key, value)�� item���� ������ ArrayList --> myData
    // �� Dir name(ex, App,Contents...) --> name
    // ApiDemos Activity(�ڱ��ڽ���)�� ȣ���� intent 
    //  --> dir sub menu�� ������ act�� ������ ���� activity�� �ȴ�.
    protected void addItem(List<Map<String, Object>> data, String name, Intent intent) {
        Map<String, Object> temp = new HashMap<String, Object>();
        //temp.put������ temp���ٰ� <string,Object>�� �� item�� ���� put���ش�.
        //temp�� �׻� Map<string, ?> �� �� format�̾�� �Ѵ�.
        temp.put("title", name);
        //���� temp�� item���� ���� <String, Object>�� temp.put method���� new���ش�.
        //ArrayList->HashMap->HashMap$Entry.<String, Object>
        //reference->dir�̸�->sub dir�̸�, sub dir�� menu act�� �ɼ��� �ְ�, ���� act�� �ɼ��� �ִ� 
        temp.put("intent", intent);
        data.add(temp);
    }

    // ���� menu�� �����ִ� list act���� �ش� item�� click������ ȣ��
    @Override
    @SuppressWarnings("unchecked")
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Map<String, Object> map = (Map<String, Object>)l.getItemAtPosition(position);
        //reference->dir�̸�->sub dir�̸�, sub dir�� menu act�� �ɼ��� �ְ�, ���� act�� �ɼ��� �ִ� 
        //�׷��Ƿ� ���� act�� ���� act�� ���۽�Ű��, listact�� ��ӹ��� menu act�� menu act�� �����Ѵ�.
        //���� menu act�� ���� class�̹Ƿ� oncreate���� list menu�� draw���ְ� �ǰ�,
        //���� act�� �� act�� oncreate�� �����ϰ� �ȴ�.
        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }
}
