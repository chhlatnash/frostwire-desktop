package com.frostwire.gnutella.gui.android;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.frostwire.gnutella.gui.android.Device.OnActionFailedListener;
import com.limegroup.gnutella.gui.I18n;

public class DeviceBar extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6886611714952957959L;
	
	private Map<Device, DeviceButton> _buttons;
	private MyOnActionFailedListener _deviceListener;
	private MyMouseAdapter _mouseAdapter;
	
	private Device _selectedDevice;
	
	public DeviceBar() {
		setupUI();
		
		_buttons = new HashMap<Device, DeviceButton>();
		_deviceListener = new MyOnActionFailedListener();
		_mouseAdapter = new MyMouseAdapter();
	}

	public void handleNewDevice(Device device) {
		
		DeviceButton button = new DeviceButton(device);
		button.addMouseListener(_mouseAdapter);
		_buttons.put(device, button);
		add(button);
		revalidate();
		
		device.setOnActionFailedListener(_deviceListener);
	}

	public void handleDeviceAlive(Device device) {
		// nothing for now
	}
	
	public void handleDeviceStale(final Device device) {
		DeviceButton button = _buttons.remove(device);

		if (button != null) {
			remove(button);
			revalidate();

			if (_buttons.size() == 0) {
				AndroidMediator.instance().getDeviceExplorer().setPanelDevice(false);
			}
		}
	}

	public Device getSelectedDevice() {
		return _selectedDevice;
	}
	
	protected void setupUI() {
		setLayout(new FlowLayout());
		setPreferredSize(new Dimension(300, 100));
	}
	
	private final class MyMouseAdapter extends MouseAdapter {
		public void mouseClicked(MouseEvent e) {
			DeviceButton button = (DeviceButton) e.getComponent();
			_selectedDevice = button.getDevice();
		}
	}
	
	private final class MyOnActionFailedListener implements OnActionFailedListener {
		public void onActionFailed(Device device, int action, Exception e) {
			JComponent dialogParent = AndroidMediator.instance().getComponent();
			if (action == Device.ACTION_UPLOAD) {
				JOptionPane.showMessageDialog(dialogParent, I18n.tr("You are not authorized to upload files to this device"), I18n.tr("From ") + device.getName(), JOptionPane.INFORMATION_MESSAGE);
			} else {
				handleDeviceStale(device);
				JOptionPane.showMessageDialog(dialogParent, I18n.tr("Error connecting to device: ") + (e != null ? e.getMessage() : I18n.tr("undefined")), I18n.tr("From ") + device.getName(), JOptionPane.OK_OPTION);
			}
		}
	}
}
