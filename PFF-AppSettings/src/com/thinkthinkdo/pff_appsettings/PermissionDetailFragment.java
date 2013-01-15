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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.thinkthinkdo.pff_appsettings.R;
import com.thinkthinkdo.pff_appsettings.content.PFFSettingsContent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PermissionInfo;

public class PermissionDetailFragment extends Fragment {

    private final static boolean localLOGV = false;
    public static final String ARG_ITEM_ID = "item_id";
    private final static String TAG = "PermissionDetailFragment";

    PFFSettingsContent.PFFSettingsContentItem mItem;
    PackageManager mPm;
    private Context mContext;
    private LayoutInflater mInflater;

    static class MyPermissionInfo extends PermissionInfo {
        CharSequence mLabel;

        /**
         * PackageInfo.requestedPermissionsFlags for the new package being installed.
         */
        int mNewReqFlags;

        /**
         * PackageInfo.requestedPermissionsFlags for the currently installed
         * package, if it is installed.
         */
        int mExistingReqFlags;

        /**
         * True if this should be considered a new permission.
         */
        boolean mNew;
        
        MyPermissionInfo() {
        }

        MyPermissionInfo(PermissionInfo info) {
            super(info);
        }

        MyPermissionInfo(MyPermissionInfo info) {
            super(info);
            mNewReqFlags = info.mNewReqFlags;
            mExistingReqFlags = info.mExistingReqFlags;
            mNew = info.mNew;
        }
    }

    public static class AppItemView extends LinearLayout implements View.OnClickListener {

        PackageManager mPm;
        private final static boolean localLOGV = false;

        private final static String TAG = "PermissionItemView";

        private HashSet<String> mSpoofedPerms;
        private HashSet<String> mSpoofablePerms;
        
        private EditableChangeListener mEditableChangeListener = new EditableChangeListener();

        private class EditableChangeListener implements CompoundButton.OnCheckedChangeListener{

            private final static String TAG = "EditableChangeListener";

            @Override
            public void onCheckedChanged (CompoundButton buttonView, boolean isChecked){
                final int id = buttonView.getId();
                final MyPermissionInfo perm = (MyPermissionInfo) buttonView.getTag(); 
                Log.i(TAG, "pff onCheckedChanged got Tag: perm.name="+perm.name+" PackageName="+perm.packageName);
                switch (id) {
                case R.id.spoof_button:
                	boolean on = ((android.widget.Switch) buttonView).isChecked();
                	if (on)
                		spoofPerm(perm);
                	else
                		unspoofPerm(perm);
                    break;
                }       	
            }
                    
            private void spoofPerm(final MyPermissionInfo perm) {
                PackageManager pm = getContext().getPackageManager();
                Log.i(TAG, "spoofPerm: perm.name="+perm.name+" PackageName="+perm.packageName);
                if (!mSpoofedPerms.contains(perm.name)) {
                	pm.setSpoofedPermissions(perm.packageName,
                            addPermToList(mSpoofedPerms, perm));
                }
            }

            private void unspoofPerm(final MyPermissionInfo perm) {
                PackageManager pm = getContext().getPackageManager();
                Log.i(TAG, "unspoofPerm: perm.name="+perm.name+" PackageName="+perm.packageName);
                if (mSpoofedPerms.contains(perm.name)) {
                    pm.setSpoofedPermissions(perm.packageName,
                            removePermFromList(mSpoofedPerms, perm));
                }
            }
            
            private String[] addPermToList(final HashSet<String> set, final MyPermissionInfo perm) {
                set.add(perm.name);
                final String[] rp = new String[set.size()];
                set.toArray(rp);
                return rp;
            }

            private String[] removePermFromList(final HashSet<String> set, final MyPermissionInfo perm) {
                set.remove(perm.name);
                final String[] rp = new String[set.size()];
                set.toArray(rp);
                return rp;
            }
            
        }
        
        public AppItemView(Context context, AttributeSet attrs) {
            super(context, attrs);
            setClickable(true);
            mPm = getContext().getPackageManager();
        }
    	
        @Override
        public void onClick(View v) {
        	
        }

        public void setApp(MyPermissionInfo perm) {

            mSpoofedPerms = new HashSet<String>();
        	String[] spoofed = mPm.getSpoofedPermissions(perm.packageName);
            mSpoofedPerms.addAll(Arrays.asList(spoofed));
        	
            if (localLOGV) Log.i(TAG, "pff: AppItemView.setApp packageName="+perm.packageName);

            TextView appNameView = (TextView) findViewById(R.id.app_name);
            try {
            	ApplicationInfo appInfo = mPm.getApplicationInfo(perm.packageName, PackageManager.GET_PERMISSIONS);
                appNameView.setText(appInfo.loadLabel(mPm).toString());
            } catch (NameNotFoundException e) {
                Log.w(TAG, "pff: AppItemView.setApp Couldn't retrieve ApplicationInfo for package:"+perm.packageName);
                return;
            }
            Switch spoofSwitch = (Switch) findViewById(R.id.spoof_button);
            if (null != spoofSwitch){
	            spoofSwitch.setVisibility(View.VISIBLE);
	            spoofSwitch.setTag(perm);
	            spoofSwitch.setOnCheckedChangeListener(mEditableChangeListener);
	            
	            if (mSpoofedPerms.contains(perm.name)) {
	                if (localLOGV) Log.i(TAG, "pff: AppItemView.setApp perm.name=" + 
	                		perm.name + " spoofed");            	
	                spoofSwitch.setChecked(true);
	            } else {
	                if (localLOGV) Log.i(TAG, "pff: AppItemView.setApp perm.name=" + 
	                		perm.name + " not spoofed");            	            	
	                spoofSwitch.setChecked(false);
	            }
            }
        }
        
    }
    
    public PermissionDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getActivity();
        mInflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        mPm = (PackageManager)this.getActivity().getPackageManager();

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            mItem = PFFSettingsContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_permission_detail, container, false);
        LinearLayout displayList = (LinearLayout) rootView.findViewById(R.id.apps_list);
        
        if (mItem != null) {
        	String permName = "android.permission." + mItem.content;
        	this.displayApps(displayList, permName);
        }
        return rootView;
    }
    
    private void displayApps(LinearLayout permListView, String permName) {
        List<PackageInfo> packs = mPm.getInstalledPackages(0);
        SortedMap<String,MyPermissionInfo> usedPerms = new TreeMap<String,MyPermissionInfo>();
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            PackageInfo pkgInfo;
            try {
                pkgInfo = mPm.getPackageInfo(p.packageName, PackageManager.GET_PERMISSIONS);
            } catch (NameNotFoundException e) {
                Log.w(TAG, "Couldn't retrieve permissions for package:"+p.packageName);
                continue;
            }
            // Extract all user permissions
            Set<MyPermissionInfo> permSet = new HashSet<MyPermissionInfo>();
            if((pkgInfo.applicationInfo != null) && (pkgInfo.applicationInfo.uid != -1)) {
                if (localLOGV) Log.w(TAG, "getAllUsedPermissions package:"+p.packageName);
                getAllUsedPermissions(pkgInfo.applicationInfo.uid, permSet);
            }
            for(MyPermissionInfo tmpInfo : permSet) {
                if (localLOGV) Log.i(TAG, "tmpInfo package:"+p.packageName+", tmpInfo.name="+tmpInfo.name);
            	if (tmpInfo.name.equalsIgnoreCase(permName)){
                    if (localLOGV) Log.w(TAG, "Adding package:"+p.packageName);
                    tmpInfo.packageName = p.packageName;
                    try {
                	ApplicationInfo appInfo = mPm.getApplicationInfo(tmpInfo.packageName, PackageManager.GET_PERMISSIONS);
            		usedPerms.put(appInfo.loadLabel(mPm).toString(),tmpInfo);
                    } catch (NameNotFoundException e) {
                        Log.w(TAG, "Couldn't retrieve permissions for package:"+tmpInfo.packageName);
                        return;
                    }
            	}
            }
        }
        permListView.removeAllViews();
        int j = 0;
        int spacing = (int)(8*mContext.getResources().getDisplayMetrics().density);
        for(Map.Entry<String,MyPermissionInfo> entry : usedPerms.entrySet()){
    	    MyPermissionInfo tmpPerm = entry.getValue();
            if (localLOGV) Log.w(TAG, "usedPacks containd package:"+tmpPerm.packageName);
            View view = getAppItemView( mContext, mInflater, tmpPerm);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            if (j == 0) {
                lp.topMargin = spacing;
            }
            if (permListView.getChildCount() == 0) {
                lp.topMargin *= 2;
            }
            permListView.addView(view, lp);
            j++;
        }    	
    }

    private static AppItemView getAppItemView(Context context, LayoutInflater inflater, MyPermissionInfo perm) {
    	AppItemView appView = (AppItemView)inflater.inflate(R.layout.app_permission_item, null);
    	appView.setApp(perm);
        return appView;
    }
    
	private void getAllUsedPermissions(int sharedUid, Set<MyPermissionInfo> permSet) {
        String sharedPkgList[] = mPm.getPackagesForUid(sharedUid);
        if(sharedPkgList == null || (sharedPkgList.length == 0)) {
            return;
        }
        for(String sharedPkg : sharedPkgList) {
            getPermissionsForPackage(sharedPkg, permSet);
        }
	}
	
    private void getPermissionsForPackage(String packageName, 
            Set<MyPermissionInfo> permSet) {
        PackageInfo pkgInfo;
        try {
            pkgInfo = mPm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
        } catch (NameNotFoundException e) {
            Log.w(TAG, "Couldn't retrieve permissions for package:"+packageName);
            return;
        }
        if ((pkgInfo != null) && (pkgInfo.requestedPermissions != null)) {
            extractPerms(pkgInfo, permSet, pkgInfo);
        }
    }

    private void extractPerms(PackageInfo info, Set<MyPermissionInfo> permSet,
            PackageInfo installedPkgInfo) {
        String[] strList = info.requestedPermissions;
//        int[] flagsList = info.requestedPermissionsFlags;
        if ((strList == null) || (strList.length == 0)) {
            return;
        }
//        mInstalledPackageInfo = installedPkgInfo;
        for (int i=0; i<strList.length; i++) {
            String permName = strList[i];
            // If we are only looking at an existing app, then we only
            // care about permissions that have actually been granted to it.
            if (installedPkgInfo != null && info == installedPkgInfo) {
//                if ((flagsList[i]&PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0) {
//                    continue;
//                }
            }
            try {
                PermissionInfo tmpPermInfo = mPm.getPermissionInfo(permName, 0);
                if (tmpPermInfo == null) {
                    continue;
                }
                int existingIndex = -1;
                if (installedPkgInfo != null
                        && installedPkgInfo.requestedPermissions != null) {
                    for (int j=0; j<installedPkgInfo.requestedPermissions.length; j++) {
                        if (permName.equals(installedPkgInfo.requestedPermissions[j])) {
                            existingIndex = j;
                            break;
                        }
                    }
                }
/*                final int existingFlags = existingIndex >= 0 ?
                        installedPkgInfo.requestedPermissionsFlags[existingIndex] : 0;
                if (!isDisplayablePermission(tmpPermInfo, flagsList[i], existingFlags)) {
                    // This is not a permission that is interesting for the user
                    // to see, so skip it.
                    continue;
                } */
                final String origGroupName = tmpPermInfo.group;
                String groupName = origGroupName;
                if (groupName == null) {
                    groupName = tmpPermInfo.packageName;
                    tmpPermInfo.group = groupName;
                }
/*                MyPermissionGroupInfo group = mPermGroups.get(groupName);
                if (group == null) {
                    PermissionGroupInfo grp = null;
                    if (origGroupName != null) {
                        grp = mPm.getPermissionGroupInfo(origGroupName, 0);
                    }
                    if (grp != null) {
                        group = new MyPermissionGroupInfo(grp);
                    } else {
                        // We could be here either because the permission
                        // didn't originally specify a group or the group it
                        // gave couldn't be found.  In either case, we consider
                        // its group to be the permission's package name.
                        tmpPermInfo.group = tmpPermInfo.packageName;
                        group = mPermGroups.get(tmpPermInfo.group);
                        if (group == null) {
                            group = new MyPermissionGroupInfo(tmpPermInfo);
                        }
                        group = new MyPermissionGroupInfo(tmpPermInfo);
                    }
                    mPermGroups.put(tmpPermInfo.group, group);
                } */
                final boolean newPerm = installedPkgInfo != null;
//                        && (existingFlags&PackageInfo.REQUESTED_PERMISSION_GRANTED) == 0;
                MyPermissionInfo myPerm = new MyPermissionInfo(tmpPermInfo);
//                myPerm.mNewReqFlags = flagsList[i];
//                myPerm.mExistingReqFlags = existingFlags;
                // This is a new permission if the app is already installed and
                // doesn't currently hold this permission.
                myPerm.mNew = newPerm;
                permSet.add(myPerm);
            } catch (NameNotFoundException e) {
                Log.i(TAG, "Ignoring unknown permission:"+permName);
            }
        }
    }
    
}
