package org.khmeracademy.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cocosw.bottomsheet.BottomSheet;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.khmeracademy.Adapter.ReplyCommentAdapter;
import org.khmeracademy.CallBack.RecyclerEventListener;
import org.khmeracademy.Model.CommentItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.VolleySingleton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReplyCommentFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private ImageView imgSend;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CommentItem> commentItems = new ArrayList<>();
    private String video_id, root_comment_id, user_id, user_name;
    private SwipeRefreshLayout mSwipeRefreshWidget;
    LinearLayout back;
    private EditText edComment;
    private String date = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
    // Pagination
    private int rowCount = 10, pageCount = 1, totalRecords, totalPages, remainOfRecords;
    // Comment Helper
    private int currentPosition;
    private String comment_id;
    // Check whether add new or update comment
    private boolean addNewComment = true;

    public static ReplyCommentFragment newInstance(String root_comment_id, String video_id) {
        ReplyCommentFragment frag = new ReplyCommentFragment();
        Bundle args = new Bundle();
        args.putString("root_comment_id", root_comment_id);
        args.putString("video_id", video_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get Arguments
        root_comment_id = getArguments().getString("root_comment_id", "N/A");
        video_id = getArguments().getString("video_id", "N/A");
        user_id = getActivity().getSharedPreferences("userSession", 0).getString("id", "N/A");
        user_name = getActivity().getSharedPreferences("userSession", 0).getString("userName", "N/A");

        // Inflate the layout for this fragment
        View view = inflater.inflate(org.khmeracademy.R.layout.reply_comment_fragment, container, false);
        initViews(view);

        if (edComment.getText().toString().equals("")) {
            imgSend.setEnabled(false);
            imgSend.setImageResource(org.khmeracademy.R.drawable.send_disable);
        }

        edComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgSend.setEnabled(false);
                    imgSend.setImageResource(org.khmeracademy.R.drawable.send_disable);
                } else {
                    imgSend.setEnabled(true);
                    imgSend.setImageResource(org.khmeracademy.R.drawable.send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //setOnRefreshListener of swipeRefreshWidget
        mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ReplyCommentAdapter(commentItems);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());

        mRecyclerView.addOnItemTouchListener(new RecyclerEventListener(getActivity(), new RecyclerEventListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {

            }

            @Override
            public void onItemLongPress(View childView, final int position) {
                if (getActivity().getSharedPreferences("userSession", 0).getString("id", "N/A").equals(commentItems.get(position).getUserId())) {
                    new BottomSheet.Builder(getActivity()).title("សូមជ្រើសរើស").sheet(org.khmeracademy.R.menu.pop_up_edit_comment).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case org.khmeracademy.R.id.delete:
                                    deleteComment(commentItems.get(position).getCmt_id(), position);
                                    break;
                                case org.khmeracademy.R.id.edit:
                                    comment_id = commentItems.get(position).getCmt_id();
                                    edComment.setText(commentItems.get(position).getCmt_text());
                                    edComment.requestFocus();
                                    edComment.setSelection(commentItems.get(position).getCmt_text().length());
                                    currentPosition = position;
                                    addNewComment = false;
                                    break;
                            }
                        }
                    }).show();
                }

            }
        }));

        //calling method getComment
        getComment(pageCount, rowCount);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Insert or Update comment on 1 button */
                if (addNewComment) {
                    addReplyComment(edComment.getText().toString(), video_id, root_comment_id, user_id);
                } else {
                    updateComment(edComment.getText().toString(), video_id, comment_id, root_comment_id, user_id, currentPosition);
                }
            }
        });

        return view;
    }

    /*Calculate total pages for pagination */
    public int getTotalPages(int totalRecords) {
        if (totalRecords >= rowCount) {
            totalPages = totalRecords / rowCount;
            remainOfRecords = totalRecords % rowCount;
            if (remainOfRecords > 0) totalPages++;
        } else {
            totalPages = 1;
        }
        return totalPages;
    }

    /* Refresh List When User Swipe List View  */
    public void refreshList() {
        //totalPages++;
        if (pageCount < totalPages) {
            pageCount++;
            getComment(pageCount, rowCount);
        } else {
            Toast.makeText(getContext(), "មិនមានការឆ្លើយតបចាស់ៗទេ", Toast.LENGTH_LONG).show();
        }
        mSwipeRefreshWidget.setRefreshing(false);
    }

    private void getComment(int pageCount, int rowCount) {
        String url = API.listReplyCommentByCommentId + "v/" + video_id + "/r/" + root_comment_id + "?page=" + pageCount + "&item=" + rowCount;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("PAGINATION")) {
                        JSONObject jsonPagination = response.getJSONObject("PAGINATION");
                        totalRecords = jsonPagination.getInt("totalCount");
                        totalPages = getTotalPages(totalRecords);
                    }

                    // If logIn Successfully
                    if (response.has("RES_DATA")) {
                        JSONArray jsonArray = response.getJSONArray("RES_DATA");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            CommentItem cmt = new CommentItem();
                            cmt.setCmt_id(jsonObject.getString("commentId"));
                            cmt.setCmt_text(jsonObject.getString("commentText"));
                            cmt.setCmt_date(jsonObject.getString("commentDate"));
                            cmt.setUserName(jsonObject.getString("username"));
                            cmt.setUserId(jsonObject.getString("userId"));
                            commentItems.add(0, cmt);
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), "មិនមានការឆ្លើយតបទេ", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    mAdapter.notifyDataSetChanged();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), org.khmeracademy.R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void addReplyComment(final String comment_text, final String video_id, final String root_comment_id, final String user_id) {
        imgSend.setEnabled(false);
        imgSend.setImageResource(org.khmeracademy.R.drawable.send_disable);
        JSONObject param = new JSONObject();
        try {
            param.put("commentText", comment_text);
            param.put("videoId", video_id);
            param.put("userId", user_id);
            param.put("replyId", root_comment_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.addReplyComment, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("STATUS")) {
                        if (response.getBoolean("STATUS")) {
                            CommentItem obj_commentItem = new CommentItem();
                            obj_commentItem.setCmt_text(comment_text);
                            obj_commentItem.setUserName(user_name);
                            obj_commentItem.setCmt_date(date);
                            obj_commentItem.setUserId(user_id);
                            obj_commentItem.setCmt_id(response.getString("COMMENTID"));
                            commentItems.add(obj_commentItem);
                            edComment.setText("");
                            mAdapter.notifyItemInserted(commentItems.size() - 1);
                            Toast.makeText(getActivity(), "ឆ្លើយតបបានសំរេច", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "ឆ្លើយតបមិនបានសំរេច", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imgSend.setEnabled(true);
                imgSend.setImageResource(org.khmeracademy.R.drawable.send);
                Toast.makeText(getActivity(), org.khmeracademy.R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void deleteComment(final String comment_id, final int position) {
        String url = API.deleteReplyComment + comment_id;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.DELETE, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("STATUS")) {
                        if (response.getBoolean("STATUS")) {
                            commentItems.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                    } else {
                        Toast.makeText(getActivity(), "Delete comment Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), org.khmeracademy.R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void updateComment(final String comment_text, final String video_id, final String comment_id, final String root_comment_id, final String user_id, final int position) {
        JSONObject param = new JSONObject();
        try {
            param.put("commentId", comment_id);
            param.put("commentText", comment_text);
            param.put("videoId", video_id);
            param.put("userId", user_id);
            param.put("replyId", root_comment_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.PUT, API.updateReplyComment, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("STATUS")) {
                        if (response.getBoolean("STATUS")) {
                            commentItems.get(position).setCmt_text(edComment.getText().toString());
                            edComment.setText("");
                            mAdapter.notifyItemChanged(position);
                            Toast.makeText(getActivity(), "Update Successfully", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Update comment Failed!", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    addNewComment = true;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), org.khmeracademy.R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void initViews(View view) {
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(org.khmeracademy.R.id.swipe_refresh_widget);
        edComment = (EditText) view.findViewById(org.khmeracademy.R.id.ed_comment);
        mRecyclerView = (RecyclerView) view.findViewById(org.khmeracademy.R.id.recycler_reply_comment);
        imgSend = (ImageView) view.findViewById(org.khmeracademy.R.id.imgSend);
        back = (LinearLayout) view.findViewById(org.khmeracademy.R.id.headerLayout);
    }

}
