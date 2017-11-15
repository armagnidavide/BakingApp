package com.example.android.bakingapp;

import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViewsService;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        Log.e("BakingAppDebug","ListWidgetService onGetViewFactory() " );
        return new ListRemoteViewFactory(this.getApplicationContext(),intent);
    }
}
