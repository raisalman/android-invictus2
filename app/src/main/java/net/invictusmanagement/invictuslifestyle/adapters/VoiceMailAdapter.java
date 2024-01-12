package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.activities.TabbedActivity;
import net.invictusmanagement.invictuslifestyle.fragments.VoiceMailFragment;
import net.invictusmanagement.invictuslifestyle.interfaces.VoiceMailFragmentInteractionListener;
import net.invictusmanagement.invictuslifestyle.models.VoiceMail;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;
import net.invictusmanagement.invictuslifestyle.webservice.MobileDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import me.samlss.lighter.Lighter;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.RectShape;

public class VoiceMailAdapter extends RecyclerView.Adapter<VoiceMailAdapter.ViewHolder> {

    private final List<VoiceMail> _dataSource = new ArrayList<>();
    private final VoiceMailFragmentInteractionListener _listener;
    private VoiceMailFragment voiceMailFragment;
    boolean isRWTVoiceMail;

    public VoiceMailAdapter(VoiceMailFragmentInteractionListener listener, VoiceMailFragment voiceMailFragment, boolean isRWTVoiceMail) {
        _listener = listener;
        this.voiceMailFragment = voiceMailFragment;
        this.isRWTVoiceMail = isRWTVoiceMail;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_voice_mail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.item = _dataSource.get(position);

        if (isRWTVoiceMail) {
            if (position == 0) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        walkThroughHighlight(holder.button_watch, holder.button_download, holder.button_delete);
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(TabbedActivity.tabbedActivity);
                        sharedPreferences.edit().putBoolean("isRWTVoiceMail", false).apply();
                        isRWTVoiceMail = sharedPreferences.getBoolean("isRWTVoiceMail", true);
                    }
                }, 500);
            }
        }
        // holder.titleTextView.setText(holder.item.videoName);
        DateFormat formatter = SimpleDateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.SHORT);
        holder.createdUtcTextView.setText(formatter.format(holder.item.createdUtc));

        if (holder.item.isRead) {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_read));
            holder.createdUtcTextView.setTextColor(ContextCompat.getColor(holder.createdUtcTextView.getContext(), R.color.black_757575));
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.titleTextView.getContext(), R.color.black_757575));
        } else {
            holder.viewMain.setBackgroundColor(holder.viewMain.getContext().getResources().getColor(R.color.bg_un_read));
            holder.createdUtcTextView.setTextColor(ContextCompat.getColor(holder.createdUtcTextView.getContext(), android.R.color.black));
            holder.titleTextView.setTextColor(ContextCompat.getColor(holder.titleTextView.getContext(), android.R.color.black));
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != _listener) {
                    _listener.onListFragmentInteraction(holder.item);
                }
            }
        });
        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.button_delete.getContext());
                builder.setMessage("Are you sure you want to delete this voice mail?")
                        .setTitle("Voice Mail");
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteVoiceMail(holder.item);
                    }
                });
                builder.create().show();
            }
        });
        holder.button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                voiceMailFragment.downloadFile(holder.item);
            }
        });
        holder.button_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchVoice(holder.item, position);
                _listener.watchVideo(holder.item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }


    public void refresh(List<VoiceMail> list) {
        if (list == null) return;
        _dataSource.clear();
        _dataSource.addAll(list);
    }

    public void watchVoice(final VoiceMail item, final int position) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().watchVoice(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    notifyDataSetChanged();
                    _dataSource.get(position).isRead = true;
                    voiceMailFragment.setErrorView();
                }
            }
        }.execute();
    }

    public void deleteVoiceMail(final VoiceMail item) {

        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Boolean doInBackground(Void... args) {
                try {
                    MobileDataProvider.getInstance().deleteVoiceMail(item);
                    return true;
                } catch (Exception ex) {
                    Log.e(Utilities.TAG, Log.getStackTraceString(ex));
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    _dataSource.remove(item);
                    notifyDataSetChanged();
                    voiceMailFragment.setErrorView();
                }
            }
        }.execute();
    }

    private void walkThroughHighlight(View view1, View view2, View view3) {

        Lighter.with(TabbedActivity.tabbedActivity)
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view1)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_voice_mail_watch)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(130, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(view2)
                        .setLighterShape(new RectShape())
                        .setTipLayoutId(R.layout.layout_voice_mail_download)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(50, 0, 30, 0))
                        .build())
                .addHighlight(new LighterParameter.Builder()
                        .setLighterShape(new RectShape())
                        .setHighlightedView(view3)
                        .setTipLayoutId(R.layout.layout_voice_mail_delete)
                        .setTipViewRelativeOffset(new MarginOffset(0, 0, 30, 0))
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .build())
                .show();


    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view, viewMain;
        public final TextView titleTextView;
        public final TextView createdUtcTextView;
        public final Button button_delete, button_download, button_watch;
        public VoiceMail item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            viewMain = view.findViewById(R.id.viewMain);
            titleTextView = (TextView) view.findViewById(R.id.title);
            button_delete = (Button) view.findViewById(R.id.button_delete);
            button_download = (Button) view.findViewById(R.id.button_download);
            button_watch = (Button) view.findViewById(R.id.button_watch);
            createdUtcTextView = (TextView) view.findViewById(R.id.createdUtc);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + titleTextView.getText() + "'";
        }
    }
}
