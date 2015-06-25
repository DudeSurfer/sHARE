package com.swagger.navneeeth99.share;

/**
 * Created by Yanch on 25/6/2015.
 */
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {
    /**
     * this method is called every 30 mins as specified on widgetinfo.xml
     * this method is also called on every phone reboot
     **/

    @Override
    public void onUpdate(Context context, AppWidgetManager
            appWidgetManager,int[] appWidgetIds) {

/*int[] appWidgetIds holds ids of multiple instance
 * of your widget
 * meaning you are placing more than one widgets on
 * your homescreen*/
        for (int appWidgetId : appWidgetIds) {
//            RemoteViews remoteViews = updateWidgetListView(context,
//                    appWidgetId);
//            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.listViewWidget);


            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(),R.layout.widget_layout);

            // Create an Intent to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);

            // This is needed to make this intent different from its previous intents
            intent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));

            // Creating a pending intent, which will be invoked when the user
            // clicks on the widget
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                    intent,PendingIntent.FLAG_UPDATE_CURRENT);

            //  Attach an on-click listener
            remoteViews.setOnClickPendingIntent(R.id.openButton, pendingIntent);

            // Create an Intent to launch NotesActivity
            Intent addIntent = new Intent(context, NotesActivity.class);
            // This is needed to make this intent different from its previous intents
            addIntent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
            addIntent.putExtra("ADD NOTES", true);
            // Creating a pending intent, which will be invoked when the user
            // clicks on the widget
            PendingIntent pendingAddIntent = PendingIntent.getActivity(context, 0,
                    addIntent,PendingIntent.FLAG_UPDATE_CURRENT);

            //  Attach an on-click listener
            remoteViews.setOnClickPendingIntent(R.id.addButton, pendingAddIntent);


            appWidgetManager.updateAppWidget(appWidgetId,
                    remoteViews);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

//    private RemoteViews updateWidgetListView(Context context,
//                                             int appWidgetId) {
//
//        //which layout to show on widget
//        RemoteViews remoteViews = new RemoteViews(
//                context.getPackageName(),R.layout.widget_layout);
//
////        //RemoteViews Service needed to provide adapter for ListView
////        Intent svcIntent = new Intent(context, WidgetService.class);
////
////        //passing app widget id to that RemoteViews Service
////        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
////
////        //setting a unique Uri to the intent
////        svcIntent.setData(Uri.parse(
////                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
////
////        //setting adapter to listview of the widget
////        remoteViews.setRemoteAdapter(R.id.listViewWidget,
////                svcIntent);
////
////        //setting an empty view in case of no data
//////        remoteViews.setEmptyView(R.id.listViewWidget, R.id.empty_view);
//
//        // Create an Intent to launch MainActivity
//        Intent intent = new Intent(context, MainActivity.class);
//
//        // This is needed to make this intent different from its previous intents
//        intent.setData(Uri.parse("tel:/"+ (int)System.currentTimeMillis()));
//
//        // Creating a pending intent, which will be invoked when the user
//        // clicks on the widget
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
//                intent,PendingIntent.FLAG_UPDATE_CURRENT);
//
//        //  Attach an on-click listener
//        remoteViews.setOnClickPendingIntent(R.id.openButton, pendingIntent);
//
//        return remoteViews;
//    }




}
