package com.example.android.bakingapp.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * The service to be connected to for a remote adapter to request RemoteViews.
 * Extend the RemoteViewsService to provide the appropriate RemoteViewsFactory's used to populate the remote collection view
 */
public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsService.RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewFactory(this.getApplicationContext(),intent);
    }
}
