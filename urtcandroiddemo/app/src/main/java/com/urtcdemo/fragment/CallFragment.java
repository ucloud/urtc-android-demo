package com.urtcdemo.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.urtcdemo.R;


public class CallFragment extends Fragment {
  private ImageButton cameraSwitchButton;
  private ImageButton toggleCamMuteButton;
  private ImageButton toggleMicMuteButton;
  private OnCallEvents callEvents;
  private boolean videoCallEnabled = true;
  private boolean videoMute = false ;
  private boolean audioMute = false ;

  public ImageButton getCameraSwitchButton() {
    return cameraSwitchButton;
  }

  public void setCameraSwitchButton(ImageButton cameraSwitchButton) {
    this.cameraSwitchButton = cameraSwitchButton;
  }

  public ImageButton getToggleCamMuteButton() {
    return toggleCamMuteButton;
  }

  public void setToggleCamMuteButton(ImageButton toggleCamMuteButton) {
    this.toggleCamMuteButton = toggleCamMuteButton;
  }

  public ImageButton getToggleMicMuteButton() {
    return toggleMicMuteButton;
  }

  public void setToggleMicMuteButton(ImageButton toggleMicMuteButton) {
    this.toggleMicMuteButton = toggleMicMuteButton;
  }

  /**
   * Call control interface for container activity.
   */
  public interface OnCallEvents {
    void onCallHangUp() ;
    void onCameraSwitch() ;
    boolean onToggleMic(boolean mute);
    boolean onToggleCamera(boolean mute);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View controlView = inflater.inflate(R.layout.fragment_call, container, false);

    ImageButton disconnectButton = controlView.findViewById(R.id.button_call_disconnect);
    cameraSwitchButton = controlView.findViewById(R.id.button_call_switch_camera);
    toggleCamMuteButton = controlView.findViewById(R.id.button_call_toggle_cam);
    toggleMicMuteButton = controlView.findViewById(R.id.button_call_toggle_mic);

    // Add buttons click events.
    disconnectButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        callEvents.onCallHangUp();
      }
    });

    cameraSwitchButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
       // callEvents.onCameraSwitch();
      }
    });

    toggleCamMuteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean enabled = callEvents.onToggleCamera(!videoMute);
      }
    });

    toggleMicMuteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        boolean enabled = callEvents.onToggleMic(!audioMute);
      }
    });

    return controlView;
  }

  @Override
  public void onStart() {
    super.onStart();

    Bundle args = getArguments();
    if (args != null) {
      videoCallEnabled = args.getBoolean("enablevieocall", true);
    }
    if (!videoCallEnabled) {
      cameraSwitchButton.setVisibility(View.INVISIBLE);
    }
  }

  // TODO(sakal): Replace with onAttach(Context) once we only support API level 23+.
  @SuppressWarnings("deprecation")
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    callEvents = (OnCallEvents) activity;
  }

  public void onMuteCamResult(boolean mute) {
    videoMute = mute ;
    if (mute) {
      toggleCamMuteButton.setBackground(getResources().getDrawable(R.mipmap.video_close));
    }else {
      toggleCamMuteButton.setBackground(getResources().getDrawable(R.mipmap.video_open));
    }
  }

  public void onMuteMicResult(boolean mute) {
    audioMute = mute ;
    if (mute) {
      toggleMicMuteButton.setBackground(getResources().getDrawable(R.mipmap.microphone_disable));
    }else {
      toggleMicMuteButton.setBackground(getResources().getDrawable(R.mipmap.microphone));
    }
  }

}
