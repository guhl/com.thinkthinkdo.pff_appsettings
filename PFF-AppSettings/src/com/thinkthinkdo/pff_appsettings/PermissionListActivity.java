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

package com.thinkthinkdo.pff_appsettings;

import com.thinkthinkdo.pff_appsettings.R;
import com.thinkthinkdo.pff_appsettings.AboutDialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;

public class PermissionListActivity extends FragmentActivity
        implements PermissionListFragment.Callbacks {

    private boolean mTwoPane;
	final public int ABOUT = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission_list);

        if (findViewById(R.id.permission_detail_container) != null) {
            mTwoPane = true;
            ((PermissionListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.permission_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	menu.add(0,ABOUT,0,"About");
    	return true;
    }   
    
    public boolean onOptionsItemSelected (MenuItem item){
    	switch (item.getItemId()) {
    		case ABOUT:
	    	AboutDialog about = new AboutDialog(this);
	    	about.setTitle(R.string.about_title);
	    	about.show();
	    	break;
    	}
    	return true;
    }    

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(PermissionDetailFragment.ARG_ITEM_ID, id);
            PermissionDetailFragment fragment = new PermissionDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.permission_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, PermissionDetailActivity.class);
            detailIntent.putExtra(PermissionDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
