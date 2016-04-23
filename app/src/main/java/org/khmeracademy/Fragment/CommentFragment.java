package org.khmeracademy.Fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.cocosw.bottomsheet.BottomSheet;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.khmeracademy.Adapter.CommentAdapter;
import org.khmeracademy.CallBack.DialogCallBack;
import org.khmeracademy.CallBack.RecyclerEventListener;
import org.khmeracademy.Model.CommentItem;
import org.khmeracademy.NetworkRequest.API;
import org.khmeracademy.NetworkRequest.GsonObjectRequest;
import org.khmeracademy.NetworkRequest.VolleySingleton;
import org.khmeracademy.R;
import org.khmeracademy.Util.Session;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CommentFragment extends Fragment {
    private RecyclerView mRecyclerView;
    private ImageView imgSend;
    private CommentAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<CommentItem> commentItems = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshWidget;
    private EditText edComment;
    private String date = new SimpleDateFormat("yyyy/MM/dd").format(Calendar.getInstance().getTime());
    // Pagination
    private int rowCount = 10, pageCount = 1, totalRecords, totalPages, remainOfRecords;
    // Comment Helper
    private int currentPosition;
    private String video_id, comment_id, user_id, user_name;
    // Check whether add new or update comment
    private boolean addNewComment = true;
    private DialogCallBack mDialogCallBack;

    public static CommentFragment newInstance(String video_id) {
        CommentFragment frag = new CommentFragment();
        Bundle args = new Bundle();
        args.putString("video_id", video_id);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // get Arguments
        video_id = getArguments().getString("video_id", "N/A");
        user_id = Session.id;
        user_name = Session.userName;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.comment_fragment, container, false);
        initViews(view);

        if (edComment.getText().toString().equals("")) {
            imgSend.setEnabled(false);
            imgSend.setImageResource(R.drawable.send_disable);
        }

        edComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    imgSend.setEnabled(false);
                    imgSend.setImageResource(R.drawable.send_disable);
                } else {
                    imgSend.setEnabled(true);
                    imgSend.setImageResource(R.drawable.send);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // setOnRefreshListener of swipeRefreshWidget
        mSwipeRefreshWidget.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new CommentAdapter(commentItems, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity()).build());

        mRecyclerView.addOnItemTouchListener(new RecyclerEventListener(getActivity(), new RecyclerEventListener.OnItemClickListener() {
            @Override
            public void onItemClick(View childView, int position) {
                FragmentTransaction trans = getChildFragmentManager().beginTransaction();
                trans.replace(R.id.root_frame, ReplyCommentFragment.newInstance(commentItems.get(position).getCmt_id(), video_id));
                trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                trans.addToBackStack("ReplyCommentFragment");
                trans.commit();
            }

            @Override
            public void onItemLongPress(View childView, final int position) {
                if (user_id.equals(commentItems.get(position).getUserId())) {
                    new BottomSheet.Builder(getActivity()).title(R.string.choose).sheet(R.menu.pop_up_edit_comment).listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case R.id.delete:
                                    deleteComment(commentItems.get(position).getCmt_id(), position);
                                    break;
                                case R.id.edit:
                                    Toast.makeText(getActivity(), "Edit", Toast.LENGTH_SHORT).show();
                                    // get comment_id for update button !
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

        // Get all comments on load
        getComment(pageCount, rowCount);

        imgSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Insert or Update comment on 1 button */
                if (addNewComment) {
                    addComment(edComment.getText().toString(), video_id, user_id);
                } else {
                    updateComment(edComment.getText().toString(), video_id, comment_id, user_id, currentPosition);
                }

            }
        });
        return view;
    }

    /* Calculate total pages for pagination */
    private int getTotalPages(int totalRecords) {
        if (totalRecords >= rowCount) {
            totalPages = totalRecords / rowCount;
            remainOfRecords = totalRecords % rowCount;
            if (remainOfRecords > 0) totalPages++;
        } else {
            totalPages = 1;
        }
        return totalPages;
    }

    /* Refresh List When User Swipe Recyclerview  */
    private void refreshList() {
        if (pageCount < totalPages) {
            pageCount++;
            getComment(pageCount, rowCount);
        } else {
            Toast.makeText(getContext(), R.string.no_old_comment, Toast.LENGTH_LONG).show();
        }
        mSwipeRefreshWidget.setRefreshing(false);
    }


    /* Get Comments */
    private void getComment(int pageCount, int rowCount) {
        String url = API.listCommentByVideoId + video_id + "?page=" + pageCount + "&item=" + rowCount + "";
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
                    if (response.has("COMMENT")) {
                        JSONArray jsonArray = response.getJSONArray("COMMENT");
                        JSONObject jsonObject;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonObject = jsonArray.getJSONObject(i);
                            CommentItem cmt = new CommentItem();
                            cmt.setCmt_id(jsonObject.getString("commentId"));
                            cmt.setCmt_text(jsonObject.getString("commentText"));
                            cmt.setCmt_date(jsonObject.getString("commentDate"));
                            cmt.setUserName(jsonObject.getString("username"));
                            cmt.setUserId(jsonObject.getString("userId"));
                            cmt.setUserImageUrl(jsonObject.getString("userImageUrl"));
                            commentItems.add(0, cmt);
                        }
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.no_comments, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    /* Add comment */
    private void addComment(final String comment_text, final String video_id, final String user_id) {
        imgSend.setEnabled(false);
        imgSend.setImageResource(R.drawable.send_disable);
        JSONObject param = new JSONObject();
        try {
            param.put("commentText", comment_text);
            param.put("videoId", video_id);
            param.put("userId", user_id);
            param.put("replyId", "MA==");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.POST, API.addComment, param, new Response.Listener<JSONObject>() {
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
                            mAdapter.notifyItemChanged(commentItems.size() - 1);
                            mRecyclerView.scrollToPosition(commentItems.size() - 1);
                            Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imgSend.setEnabled(true);
                imgSend.setImageResource(R.drawable.send);
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        // Add request queue
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    /* Delete comment */
    private void deleteComment(final String comment_id, final int position) {
        String url = API.deleteComment + comment_id;
        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.DELETE, url, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("STATUS")) {
                        if (response.getBoolean("STATUS")) {
                            commentItems.remove(position);
                            mAdapter.notifyItemRemoved(position);
                            Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    /* Update comment */
    private void updateComment(final String comment_text, final String video_id, final String comment_id, final String user_id, final int position) {
        JSONObject param = new JSONObject();
        try {
            param.put("commentId", comment_id);
            param.put("commentText", comment_text);
            param.put("videoId", video_id);
            param.put("userId", user_id);
            param.put("replyId", "MA==");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsonObjectRequest jsonRequest = new GsonObjectRequest(Request.Method.PUT, API.updateComment, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("STATUS")) {
                        if (response.getBoolean("STATUS")) {
                            commentItems.get(position).setCmt_text(edComment.getText().toString());
                            edComment.setText("");
                            mAdapter.notifyItemChanged(position);
                            Toast.makeText(getActivity(), R.string.done, Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), R.string.failed, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), R.string.internet_problem, Toast.LENGTH_LONG).show();
            }
        });
        VolleySingleton.getsInstance().addToRequestQueue(jsonRequest);
    }

    private void initViews(View view) {
        mSwipeRefreshWidget = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        edComment = (EditText) view.findViewById(R.id.ed_comment);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        imgSend = (ImageView) view.findViewById(R.id.imgSend);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mDialogCallBack = (DialogCallBack) getActivity();
        mDialogCallBack.onDismissDialog();
    }
}