/*
 * KJK_TALK APIDEMOS: App-> Menu-> Inflate from XML  NEEDTOSTUDY
 * XML�� �ƴ� �ڵ带 ����Ͽ� Spinner(List Combo Box)�� �߰��ϴ� ����� 
 * �پ��� ������ Option Menu�� ���� ����� �����ش�.
 * Option Menu(text), Option Menu(icon+text), Option Menu(text)+submenu, 
 * Option Menu(group���� ����): group���� �������� menu item�� ����ȴ�.
 * Option Menu(text)+submenu(checkable)
 * Option Menu+More+ContextMenu, 
 * Option Menu(ordered), Option Menu(2�ܰ� ordered), xml �Է¼����� �ٸ��� order�� �ټ��ִ�.
 * Option Menu(visible): menu���¿��� a,b,c�� ������ visible,hidden menu�� �����Ѵ�.
 * Option Menu(disable)+submenu(disable)
 * ����: Android���� 3������ Menu�� �����Ѵ�. Options Menu, Context Menu, SubMenu
 * Option Menu : MenuKey�� �����ϴ� �ϴܿ� �����ϴ� �޴�
        icon menu: ���簢�� box Table���� Menu�� �ִ� 6�� item�� ����
        Expanded Menu: �ϴܿ� �پ� �ִ� List Menu�� �ִ� 6�� item�� ����
   Context Menu : ȭ�鿡�� longkey�� ��� list������ Popup menu
   SubMenu: Context Menu�� ������� ����ϳ� Option/Context Menu���� sub�� ���޴�
    �Ʒ� �������� R.menu.submenu���� ���� �ִ�.


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

package com.example.android.apis.app;

import com.example.android.apis.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demonstrates inflating menus from XML. There are different menu XML resources
 * that the user can choose to inflate. First, select an example resource from
 * the spinner, and then hit the menu button. To choose another, back out of the
 * activity and start over.
 */
public class MenuInflateFromXml extends Activity {
    /**
     * Different example menu resources. 
     */// ���õ� ������ ���� Option Menu�� ����� menu resource xml
    private static final int sMenuExampleResources[] = {
        R.menu.title_only, R.menu.title_icon, R.menu.submenu, R.menu.groups,
        R.menu.checkable, R.menu.shortcuts, R.menu.order, R.menu.category_order,
        R.menu.visible, R.menu.disabled
    };
    
    /**
     * Names corresponding to the different example menu resources.
     */// Option Menu�� �����ϱ� ���� List Combo Box�� item�� ����
    private static final String sMenuExampleNames[] = {
        "Title only", "Title and Icon", "Submenu", "Groups",
        "Checkable", "Shortcuts", "Order", "Category and Order",
        "Visible", "Disabled"
    };
   
    /**
     * Lets the user choose a menu resource.
     */
    private Spinner mSpinner;

    /**
     * Shown as instructions.
     */
    private TextView mInstructionsText;
    
    /**
     * Safe to hold on to this.
     */
    private Menu mMenu;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //KJK_TALK: xml�� �ƴ� code�� layout�� �����Ѵ�.
        // Create a simple layout
        LinearLayout layout = new LinearLayout(this);
        //KJK_TALK: LinearLayout�� ����(������ �� ����)���� �׾ƶ�. 
        layout.setOrientation(LinearLayout.VERTICAL);
        
        // Create the spinner to allow the user to choose a menu XML 
        //KJK_TALK: Spinner(ListCommbo) ����, ����� ���� ���Ǵ� xml���� ���ٸ� ������ ����.
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, sMenuExampleNames); 
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = new Spinner(this);
        // When programmatically creating views, make sure to set an ID
        // so it will automatically save its instance state
        mSpinner.setId(R.id.spinner);
        mSpinner.setAdapter(adapter);//spinner�� List�� ���δ�.
        
        // Add the spinner, ������ ���� LinearLayout�� child view�� spinner�� ���̰�,layoutParam�� �����Ͽ� �����Ѵ�.
        layout.addView(mSpinner,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));

        // Create help text
        mInstructionsText = new TextView(this);
        mInstructionsText.setText(getResources().getString(
                R.string.menu_from_xml_instructions_press_menu));
        
        // Add the help, make it look decent
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(10, 10, 10, 10);//margin ����
        //������ ���� LinearLayout�� text view�� �ϳ� �߰��Ѵ�.
        layout.addView(mInstructionsText, lp);
        
        // Set the layout as our content view
        // ���ݱ��� ���� layout�� view�� ���δ�. 
        //KJK_TALK: xml�� ���϶��� ID�� ���̹Ƿ�, �̰��� �����Ͽ� XML inflate �������� ���̰� �ǳ�
        //view�� ���϶��� �̹� inflate�� intance(by creator)�� ���̰� �ǹǷ� �׳� direct�� ���δ�
        setContentView(layout);
    }

    //KJK_TALK: menu key�� �������� � option menu�� ����� �����Ѵ� 
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Hold on to this
        mMenu = menu;
        
        // Inflate the currently selected menu XML resource.
        MenuInflater inflater = getMenuInflater();
        //�ش� menu xml�� �о��
        inflater.inflate(sMenuExampleResources[mSpinner.getSelectedItemPosition()], menu);
        
        // Disable the spinner since we've already created the menu and the user
        // can no longer pick a different menu XML.
        mSpinner.setEnabled(false);//spinner�� disable��Ų��.
        
        // Change instructions
        mInstructionsText.setText(getResources().getString(
                R.string.menu_from_xml_instructions_go_back));
        
        return true;
    }

    //KJK_TALK: optionMenu���� Ư�� menu�� ���������� Ȥ�� 
    //submenu�� �ִ°�� Platform���� �ڵ����� submenu�� display���ְ� 
    //�� submenu�� item�� �ٽ� click������� �ٽ� onOptionsItemSelected�� ȣ��ȴ�.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // For "Title only": Examples of matching an ID with one assigned in
            //                   the XML
            case R.id.jump:
                Toast.makeText(this, "Jump up in the air!", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.dive:
                Toast.makeText(this, "Dive into the water!", Toast.LENGTH_SHORT).show();
                return true;

            // For "Groups": Toggle visibility of grouped menu items with
            //               nongrouped menu items
            case R.id.browser_visibility:
                // The refresh item is part of the browser group
                final boolean shouldShowBrowser = !mMenu.findItem(R.id.refresh).isVisible();
                mMenu.setGroupVisible(R.id.browser, shouldShowBrowser);
                break;
                
            case R.id.email_visibility:
                // The reply item is part of the email group
                final boolean shouldShowEmail = !mMenu.findItem(R.id.reply).isVisible();
                mMenu.setGroupVisible(R.id.email, shouldShowEmail);
                break;
                
            // Generic catch all for all the other menu resources
            default:
                // Don't toast text when a submenu is clicked
                if (!item.hasSubMenu()) {//submenu�� ������ ������ toast�� ���� 
                    Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
                    return true;
                }
                break;
        }
        
        return false;
    }
    
    

}
