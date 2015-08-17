/*
 * Copyright 2014 Hieu Rocker
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.xici.newapp.ui.picpicker;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import net.xici.newapp.R;
import net.xici.newapp.support.widget.emojicon.EmojiAdapter;
import net.xici.newapp.support.widget.emojicon.Emojicon;
import net.xici.newapp.support.widget.emojicon.EmojiconHandler;

/**
 * 表情选择
 */
public class EmojiconGridFragment extends Fragment implements
		OnItemClickListener {
	private OnEmojiconClickedListener mOnEmojiconClickedListener;
	private EmojiAdapter mEmojiAdapter;
	GridView gridView;

	protected static EmojiconGridFragment newInstance(Emojicon[] emojicons) {
		EmojiconGridFragment emojiGridFragment = new EmojiconGridFragment();
		Bundle args = new Bundle();
		args.putSerializable("emojicons", emojicons);
		emojiGridFragment.setArguments(args);
		return emojiGridFragment;
	}
	
	public EmojiconGridFragment() {
    }
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view = inflater.inflate(R.layout.emojicon_grid, container, false);
		return view;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		gridView = (GridView) view.findViewById(R.id.Emoji_GridView);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mEmojiAdapter = new EmojiAdapter(getActivity(), null);
		mEmojiAdapter.addAll(EmojiconHandler.EMOJICONS);
		gridView.setAdapter(mEmojiAdapter);
		
		gridView.setOnItemClickListener(this);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof OnEmojiconClickedListener) {
			mOnEmojiconClickedListener = (OnEmojiconClickedListener) activity;
		} else if (getParentFragment() instanceof OnEmojiconClickedListener) {
			mOnEmojiconClickedListener = (OnEmojiconClickedListener) getParentFragment();
		} else {
			throw new IllegalArgumentException(activity
					+ " must implement interface "
					+ OnEmojiconClickedListener.class.getSimpleName());
		}
	}

	@Override
	public void onDetach() {
		mOnEmojiconClickedListener = null;
		super.onDetach();
	}


	public interface OnEmojiconClickedListener {
		void onEmojiconClicked(Emojicon emojicon);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mOnEmojiconClickedListener != null) {
			mOnEmojiconClickedListener.onEmojiconClicked((Emojicon) parent
					.getItemAtPosition(position));
		}
	}
}
