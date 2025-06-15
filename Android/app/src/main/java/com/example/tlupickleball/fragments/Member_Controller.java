package com.example.tlupickleball.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.tlupickleball.R;
import com.example.tlupickleball.activities.ApproveMember;
import com.example.tlupickleball.activities.DisableMemberList;
import com.example.tlupickleball.activities.MemberList;

public class Member_Controller extends Fragment {

    LinearLayout layoutApproveList, layoutMemberList, layoutDisableMemberList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member__controller, container, false);

        layoutApproveList = view.findViewById(R.id.layoutApproveList);
        layoutApproveList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ApproveMember.class);
                startActivity(intent);
            }
        });

        layoutMemberList = view.findViewById(R.id.layoutMemberList);
        layoutMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MemberList.class);
                startActivity(intent);
            }
        });

        layoutDisableMemberList = view.findViewById(R.id.layoutDisableList);
        layoutDisableMemberList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DisableMemberList.class);
                startActivity(intent);
            }
        });

        return view;
    }
}