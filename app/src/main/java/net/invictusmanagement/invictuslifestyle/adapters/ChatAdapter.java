package net.invictusmanagement.invictuslifestyle.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import net.invictusmanagement.invictuslifestyle.R;
import net.invictusmanagement.invictuslifestyle.interfaces.ChatItemClick;
import net.invictusmanagement.invictuslifestyle.models.MessageData;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private final List<MessageData> _dataSource = new ArrayList<>();
    public RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.loader_animation)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH)
            .dontAnimate()
            .dontTransform();
    private MessageData temp = new MessageData();
    private Context _context;
    private int holderPosition = 0;
    private Date lastIndexDate;
    private ChatItemClick chatItemClick;


    public ChatAdapter(Context context, ChatItemClick chatItemClick) {
        _context = context;
        this.chatItemClick = chatItemClick;
    }

    @Override
    public int getItemViewType(int position) {
        return _dataSource.get(position).isMyMessage ? 1 : 0;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 1) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_mine, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_other, parent, false);
        }
        return new ViewHolder(view);
    }

    private String convertTime(Date datePasssed) {
        /*SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy -- K:mm a");*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("h:mm a");
        return displayFormat.format(datePasssed);
    }

    private String convertDate(Date datePasssed) {
        /*SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy -- K:mm a");*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        return displayFormat.format(datePasssed);
    }

    private Date convertDateReturnDate(Date datePasssed) {
        /*SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy -- K:mm a");*/
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            return dateFormat.parse(displayFormat.format(datePasssed));
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        temp = _dataSource.get(position);
        holderPosition = position;


        //date label
        if (position != (_dataSource.size() - 1)) {
            if (convertDateReturnDate(_dataSource.get(position + 1).createdUtc).before(convertDateReturnDate(temp.createdUtc))) {
                holder.tvChangeDate.setVisibility(View.VISIBLE);
                holder.llDate.setVisibility(View.VISIBLE);
                holder.tvChangeDate.setText(convertDate(temp.createdUtc));
            } else {
                holder.tvChangeDate.setVisibility(View.GONE);
                holder.llDate.setVisibility(View.GONE);
            }

        } else {
            holder.tvChangeDate.setVisibility(View.GONE);
            holder.llDate.setVisibility(View.GONE);
        }


        if (temp.messageType == 1) {
            //text
            holder.linearLayoutMessage.setVisibility(View.VISIBLE);
            holder.linearLayoutPicture.setVisibility(View.GONE);


        } else if (temp.messageType == 2) {
            //image
            holder.linearLayoutMessage.setVisibility(View.GONE);
            holder.linearLayoutPicture.setVisibility(View.VISIBLE);

            if (temp.isReceivedImage) {
                Glide.with(_context).load(temp.message).apply(options).into(holder.imgSent);
            } else if (temp.isLocalImage) {
                Bitmap myBitmap = BitmapFactory.decodeFile(temp.message);
                holder.imgSent.setImageBitmap(getRotateImaged(temp.message, myBitmap));
            } else {
                String str = "";
                String imgId = "";
                if (temp.message != null) {
                    str = temp.message;
                }

                if (str.contains(",")) {
                    imgId = str.substring(0, str.indexOf(","));
                } else {
                    imgId = str;
                }

                Glide.with(_context).load(temp.attachmentUrl + imgId + ".jpg").apply(options).into(holder.imgSent);
            }

        }


        String message = "";
        if (temp.isMyMessage) {
            message = "You";
        } else {
            message = temp.sender;
        }


        holder.from.setText(message);
        if (temp.message != null)
            holder.content.setText(temp.message.trim());
        if (temp.createdUtc != null)
            holder.tvTimeStamp.setText(convertTime(temp.createdUtc));

        if (temp.isRead) {
            holder.imgMessageStatus.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_read_message));
            holder.imgMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_read_message));
        } else {
            holder.imgMessageStatus.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_unread_message));
            holder.imgMessageStatusImage.setImageDrawable(ContextCompat.getDrawable(_context, R.drawable.ic_unread_message));
        }

        holder.rlMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatItemClick.onChatClick(_dataSource.get(position));
            }
        });

    }

    @Override
    public int getItemCount() {
        return _dataSource.size();
    }

    public long getLastMessageID() {
        return _dataSource.get(_dataSource.size() - 1).chatMessageId;
    }

    public void markAllRead() {
        for (int i = 0; i < _dataSource.size(); i++) {
            _dataSource.get(i).isRead = true;
        }
        notifyDataSetChanged();
    }

    public void refresh(List<MessageData> list) {
        if (list == null) return;
        /*_dataSource.clear();*/
        _dataSource.addAll(list);
        notifyDataSetChanged();
    }

    public void clearDate(List<MessageData> list) {
        if (list == null) return;
        _dataSource.clear();
        /*_dataSource.addAll(list);*/
        notifyDataSetChanged();
    }

    public void add(MessageData item) {
        if (item == null) return;
        _dataSource.add(0, item);
        /*notifyItemRangeChanged(0, getItemCount());*/
        notifyDataSetChanged();
    }

    private Bitmap getRotateImaged(String photoPath, Bitmap bitmap) {

        ExifInterface ei = null;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
        );

        Bitmap rotatedImage = null;
        if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
            rotatedImage = rotateImage(bitmap, 90f);
        } else if (orientation == ExifInterface.ORIENTATION_NORMAL) {
            rotatedImage = bitmap;
        }
        return rotatedImage;

    }

    private Bitmap rotateImage(Bitmap source, Float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public MessageData item;
        ImageView avatar, imgSent, imgMessageStatus, imgMessageStatusImage;
        TextView from, content, tvChangeDate, tvTimeStamp;
        LinearLayout llDate, linearLayoutPicture, linearLayoutMessage;
        RelativeLayout rlMain;

        public ViewHolder(View v) {
            super(v);
            this.view = v;
            rlMain = (RelativeLayout) v.findViewById(R.id.rlMain);
            llDate = (LinearLayout) v.findViewById(R.id.llDate);
            tvChangeDate = (TextView) v.findViewById(R.id.tvChangeDate);
            from = (TextView) v.findViewById(R.id.txtMessageOwner);
            linearLayoutMessage = (LinearLayout) v.findViewById(R.id.linearLayoutMessage);
            linearLayoutPicture = (LinearLayout) v.findViewById(R.id.linearLayoutPicture);
            content = (TextView) v.findViewById(R.id.txtMessageContent);
            tvTimeStamp = (TextView) v.findViewById(R.id.tvTimeStamp);
            imgSent = (ImageView) v.findViewById(R.id.imgSent);
            imgMessageStatus = (ImageView) v.findViewById(R.id.imgMessageStatus);
            imgMessageStatusImage = (ImageView) v.findViewById(R.id.imgMessageStatusImage);
        }

    }
}
