package com.example.fitnesstracker;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityFriends extends AppCompatActivity {

    ImageButton backButton;
    EditText searchFriends;
    LinearLayout friendsListContainer;
    private RequestQueue requestQueue;
    private Button followingButton;
    private Button followersButton;
    Button showFriendsButton;

    /**
     * Called when the activity is created.
     * Initializes views and sets up event listeners.
     *
     * @author Miles Nichols
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        backButton = findViewById(R.id.btn_back);
        searchFriends = findViewById(R.id.search_friends);
        friendsListContainer = findViewById(R.id.friends_list_container);
        followingButton = findViewById(R.id.btn_following);
        followersButton = findViewById(R.id.btn_followers);
        showFriendsButton = findViewById(R.id.btn_showFriends);

        String currentUsername = ActivityLogin.getUsername();
        highlightButton(showFriendsButton);
        requestQueue = Volley.newRequestQueue(this);

        searchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (!query.isEmpty()) {
                    searchUsers(query);
                } else {
                    friendsListContainer.removeAllViews();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        showFriendsButton.setOnClickListener(view -> {
            fetchFriends(currentUsername);
            highlightButton(showFriendsButton);
        });

        followingButton.setOnClickListener(view -> {
            Log.d("FriendsActivity", "Following button clicked");
            fetchFollowing(currentUsername);
            highlightButton(followingButton);
        });

        followersButton.setOnClickListener(view -> {
            Log.d("FriendsActivity", "Followers button clicked");
            fetchFollowers(currentUsername);
            highlightButton(followersButton);
        });

        backButton.setOnClickListener(view -> finish());

        fetchFriends(currentUsername);
    }

    /**
     * Highlights the active button.
     *
     * @param activeButton the button to highlight
     */
    private void highlightButton(Button activeButton) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showFriendsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            followingButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            followersButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            activeButton.setBackgroundColor(getResources().getColor(R.color.highlight_color, null));
        } else {
            showFriendsButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            followingButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            followersButton.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            activeButton.setBackgroundColor(getResources().getColor(R.color.highlight_color));
        }
    }

    /**
     * Fetches the list of friends for the given username.
     *
     * @param username the username of the current user
     */
    private void fetchFriends(String username) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/friend/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ObjectUser> friends = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject friendJson = response.optJSONObject(i);
                        if (friendJson != null) {
                            ObjectUser friend = new ObjectUser();
                            friend.setUsername(friendJson.optString("username"));
                            friends.add(friend);
                        }
                    }
                    displayFriends(friends);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Some error occurred :(", Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Displays the list of friends in the UI.
     *
     * @param friends the list of friends to display
     */
    private void displayFriends(List<ObjectUser> friends) {
        friendsListContainer.removeAllViews();

        for (ObjectUser friend : friends) {
            View friendItem = getLayoutInflater().inflate(R.layout.item_friend, null);
            TextView friendName = friendItem.findViewById(R.id.friend_name);
            friendName.setText(friend.getUsername());

            ImageButton unfollowButton = friendItem.findViewById(R.id.btn_unfollow);
            unfollowButton.setOnClickListener(view -> unfollowFriend(friend.getUsername()));

            ImageButton followButton = friendItem.findViewById(R.id.btn_follow);
            followButton.setVisibility(View.GONE);

            ImageButton messageButton = friendItem.findViewById(R.id.btn_message);
            messageButton.setOnClickListener(view -> {
                Intent chatIntent = new Intent(ActivityFriends.this, ActivityChat.class);
                chatIntent.putExtra("friend_username", friend.getUsername());
                startActivity(chatIntent);
            });

            friendsListContainer.addView(friendItem);
        }
    }

    /**
     * Follows the specified user.
     *
     * @param followingUsername the username of the user to follow
     */
    private void followFriend(String followingUsername) {
        String currentUsername = ActivityLogin.getUsername();
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/follow?follower_username=" + currentUsername + "&following_username=" + followingUsername;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Followed " + followingUsername, Toast.LENGTH_SHORT).show();
                    fetchFollowing(currentUsername);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Failed to follow: " + followingUsername, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(stringRequest);
    }

    /**
     * Fetches the list of users that the current user is following.
     *
     * @param username the username of the current user
     */
    private void fetchFollowing(String username) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/following/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ObjectUser> followingList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject followingJson = response.optJSONObject(i);
                        if (followingJson != null) {
                            ObjectUser following = new ObjectUser();
                            following.setUsername(followingJson.optString("username"));
                            followingList.add(following);
                        }
                    }
                    displayFollowing(followingList);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Some error occurred :(", Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Displays the list of users that the current user is following.
     *
     * @param following the list of users to display
     */
    private void displayFollowing(List<ObjectUser> following) {
        friendsListContainer.removeAllViews();

        for (ObjectUser user : following) {
            View userItem = getLayoutInflater().inflate(R.layout.item_friend, null);
            TextView userName = userItem.findViewById(R.id.friend_name);
            userName.setText(user.getUsername());

            ImageButton unfollowButton = userItem.findViewById(R.id.btn_unfollow);
            unfollowButton.setOnClickListener(view -> unfollowFriend(user.getUsername()));

            ImageButton followButton = userItem.findViewById(R.id.btn_follow);
            followButton.setVisibility(View.GONE);

            ImageButton messageButton = userItem.findViewById(R.id.btn_message);
            messageButton.setVisibility(View.GONE);

            friendsListContainer.addView(userItem);
        }
    }

    /**
     * Fetches the list of followers for the given username.
     *
     * @param username the username of the current user
     */
    private void fetchFollowers(String username) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/followers/" + username;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ObjectUser> followersList = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject followerJson = response.optJSONObject(i);
                        if (followerJson != null) {
                            ObjectUser follower = new ObjectUser();
                            follower.setUsername(followerJson.optString("username"));
                            followersList.add(follower);
                        }
                    }
                    displayFollowers(followersList);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Some error occurred :(", Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Displays the list of followers in the UI.
     *
     * @param followers the list of followers to display
     */
    private void displayFollowers(List<ObjectUser> followers) {
        friendsListContainer.removeAllViews();

        for (ObjectUser user : followers) {
            View userItem = getLayoutInflater().inflate(R.layout.item_friend, null);
            TextView userName = userItem.findViewById(R.id.friend_name);
            userName.setText(user.getUsername());

            ImageButton unfollowButton = userItem.findViewById(R.id.btn_unfollow);
            unfollowButton.setOnClickListener(view -> unfollowFriend(user.getUsername()));

            ImageButton followButton = userItem.findViewById(R.id.btn_follow);
            followButton.setVisibility(View.VISIBLE);
            followButton.setOnClickListener(view -> followFriend(user.getUsername()));

            ImageButton messageButton = userItem.findViewById(R.id.btn_message);
            messageButton.setVisibility(View.GONE);

            friendsListContainer.addView(userItem);
        }
    }

    /**
     * Unfollows the specified user.
     *
     * @param username the username of the user to unfollow
     */
    private void unfollowFriend(String username) {
        String currentUsername = ActivityLogin.getUsername();
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/unfollow?follower_username=" + currentUsername + "&following_username=" + username;

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Unfollowed " + username, Toast.LENGTH_SHORT).show();
                    fetchFollowing(currentUsername);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Failed to unfollow: " + username, Toast.LENGTH_LONG).show();
                }
        );

        requestQueue.add(stringRequest);
    }

    /**
     * Searches for users by a given query string.
     *
     * @param query the query string to search for
     */
    private void searchUsers(String query) {
        String url = "http://coms-3090-014.class.las.iastate.edu:8080/user/search/" + query;

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    List<ObjectUser> users = new ArrayList<>();
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject userJson = response.optJSONObject(i);
                        if (userJson != null) {
                            ObjectUser user = new ObjectUser();
                            user.setUsername(userJson.optString("username"));
                            users.add(user);
                        }
                    }
                    displaySearchResults(users);
                },
                error -> {
                    Toast.makeText(getApplicationContext(), "Some error occurred :(", Toast.LENGTH_LONG).show();
                }
        );
        requestQueue.add(jsonArrayRequest);
    }

    /**
     * Displays the search results.
     *
     * @param users the list of users to display
     */
    private void displaySearchResults(List<ObjectUser> users) {
        friendsListContainer.removeAllViews();

        for (ObjectUser user : users) {
            View userItem = getLayoutInflater().inflate(R.layout.item_friend, null);
            TextView userName = userItem.findViewById(R.id.friend_name);
            userName.setText(user.getUsername());

            ImageButton unfollowButton = userItem.findViewById(R.id.btn_unfollow);
            unfollowButton.setVisibility(View.GONE);

            ImageButton followButton = userItem.findViewById(R.id.btn_follow);
            followButton.setVisibility(View.VISIBLE);
            followButton.setOnClickListener(view -> followFriend(user.getUsername()));

            ImageButton messageButton = userItem.findViewById(R.id.btn_message);
            messageButton.setOnClickListener(view -> {
                Intent chatIntent = new Intent(ActivityFriends.this, ActivityChat.class);
                chatIntent.putExtra("friend_username", user.getUsername());
                startActivity(chatIntent);
            });

            friendsListContainer.addView(userItem);
        }
    }
}
