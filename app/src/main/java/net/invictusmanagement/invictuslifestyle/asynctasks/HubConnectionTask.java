package net.invictusmanagement.invictuslifestyle.asynctasks;

import android.os.AsyncTask;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;

import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.enum_utils.AppStatus;
import net.invictusmanagement.invictuslifestyle.fragments.HomeFragment;

public class HubConnectionTask extends AsyncTask<HubConnection, Void, Boolean> {

    public HubConnection hubConnection;
    public boolean isChatConnection;

    public HubConnectionTask(HubConnection hubConnection, boolean isChatConnection) {
        this.hubConnection = hubConnection;
        this.isChatConnection = isChatConnection;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(HubConnection... hubConnections) {
        try {
            hubConnection.start().blockingAwait();
            TabbedActivity.tabbedActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    /* Toast.makeText(TabbedActivity.tabbedActivity, hubConnection.getConnectionState().toString(), Toast.LENGTH_LONG).show();*/
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    protected void onPostExecute(Boolean aVoid) {
        if (aVoid) {
            if (hubConnection.getConnectionState() == HubConnectionState.CONNECTED) {
                if (isChatConnection) {
                    TabbedActivity.isHubConnected = true;
                    TabbedActivity.tabbedActivity.updateAppStatus(HomeFragment.userId, Integer.parseInt(AppStatus.Active.value()));
                } else {
                    TabbedActivity.isMobileHubConnected = true;
                }
            } else {
                if (isChatConnection) {
                    TabbedActivity.isHubConnected = false;
                } else {
                    TabbedActivity.isMobileHubConnected = false;
                }
            }
        } else {
            if (isChatConnection) {
                TabbedActivity.isHubConnected = false;
            } else {
                TabbedActivity.isMobileHubConnected = false;
            }
        }
    }
}