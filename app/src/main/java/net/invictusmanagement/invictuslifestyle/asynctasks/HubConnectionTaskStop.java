package net.invictusmanagement.invictuslifestyle.asynctasks;

import android.os.AsyncTask;

import com.microsoft.signalr.HubConnection;
import com.microsoft.signalr.HubConnectionState;

import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;

public class HubConnectionTaskStop extends AsyncTask<HubConnection, Void, Boolean> {

    public HubConnection hubConnection;
    public boolean isChatHubConnection;

    public HubConnectionTaskStop(HubConnection hubConnection, boolean isChatHubConnection) {
        this.hubConnection = hubConnection;
        this.isChatHubConnection = isChatHubConnection;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(HubConnection... hubConnections) {
        try {
            hubConnection.stop().blockingAwait();
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
                if (isChatHubConnection)
                    TabbedActivity.isHubConnected = true;
                else
                    TabbedActivity.isMobileHubConnected = true;
            } else {
                if (isChatHubConnection)
                    TabbedActivity.isHubConnected = false;
                else
                    TabbedActivity.isMobileHubConnected = false;
            }
        } else {
            if (isChatHubConnection)
                TabbedActivity.isHubConnected = false;
            else
                TabbedActivity.isMobileHubConnected = false;
        }

    }
}