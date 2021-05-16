package kr.o3selab.homecoco.Terms;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import kr.o3selab.homecoco.Models.TermsContent;
import kr.o3selab.homecoco.R;

public class PrivacyFragment extends Fragment {

    public PrivacyFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_privacy, null);

        TextView textView = (TextView) view.findViewById(R.id.privacy_text);
        textView.setText(TermsContent.privacy);

        return view;
    }
}
