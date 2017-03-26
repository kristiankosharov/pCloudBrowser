package pcloud.task.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pcloud.sdk.RemoteFolder;

import java.util.Date;

import pcloud.task.R;

public class ListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private RemoteFolder mRemoteFolder;
    private static final int FILE_TYPE = 0;
    private static final int FOLDER_TYPE = 1;

    public ListAdapter(Context context, RemoteFolder remoteFolder) {
        mContext = context;
        mRemoteFolder = remoteFolder;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case FILE_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.file_list_item, parent, false);
                return new FileHolder(view);
            case FOLDER_TYPE:
                view = LayoutInflater.from(mContext).inflate(R.layout.folder_list_item, parent, false);
                return new FolderHolder(view);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == FILE_TYPE) {
            FileHolder fileHolder = (FileHolder) holder;
            fileHolder.mTxtFileName.setText(mRemoteFolder.children().get(position).name());
            long size = mRemoteFolder.children().get(position).asFile().size();
            fileHolder.mTxtFileSize.setText(getMBSize(size));
            Date date = mRemoteFolder.children().get(position).asFile().lastModified();
            String dat = DateFormat.format("dd.MM.dd hh:mm", date).toString();

            fileHolder.mTxtFileModTime.setText(dat);
        } else {
            FolderHolder folderHolder = (FolderHolder) holder;
            folderHolder.mTxtFolderName.setText(mRemoteFolder.children().get(position).name());
        }
    }

    @Override
    public int getItemCount() {
        return mRemoteFolder.children().size();
    }

    @Override
    public int getItemViewType(int position) {
        if (mRemoteFolder.children().get(position).isFile()) {
            return FILE_TYPE;
        } else {
            return FOLDER_TYPE;
        }
    }

    class FileHolder extends RecyclerView.ViewHolder {

        TextView mTxtFileName;
        TextView mTxtFileModTime;
        TextView mTxtFileSize;

        public FileHolder(View itemView) {
            super(itemView);
            mTxtFileName = (TextView) itemView.findViewById(R.id.txt_file_title);
            mTxtFileModTime = (TextView) itemView.findViewById(R.id.txt_file_mod_time);
            mTxtFileSize = (TextView) itemView.findViewById(R.id.txt_file_size);
        }
    }

    class FolderHolder extends RecyclerView.ViewHolder {

        TextView mTxtFolderName;

        public FolderHolder(View itemView) {
            super(itemView);

            mTxtFolderName = (TextView) itemView.findViewById(R.id.txt_folder_title);
        }
    }

    private String getMBSize(long size) {
        int m = (int) (size / 1024.0);
        return m + "Mb";
    }
}
