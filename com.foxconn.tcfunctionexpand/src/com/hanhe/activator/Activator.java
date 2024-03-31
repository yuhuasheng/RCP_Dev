package com.hanhe.activator;

import java.util.Dictionary;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.mycom.sendtoapp.perspectives.CustomOpenService;
import com.teamcenter.rac.services.IOpenService;
import com.teamcenter.rac.services.IServiceConstants;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.foxconn.tcfunctionexpand";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
//		Dictionary<String, String> properties = new java.util.Hashtable<String, String>();
//        // Register the open service
//        properties.put( IServiceConstants.PERSPECTIVE,
//                    "com.mycom.sendtoapp.perspectives.CustomPerspective" );//perspective id
//        CustomOpenService customOpenService = new CustomOpenService();
// 
//        context.registerService( IOpenService.class.getName(),
//                customOpenService, properties );
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
//	protected void setupServices(BundleContext context) {			
//		Dictionary<String, String> properties = new java.util.Hashtable<String, String>();
//		 // Register the open service
//		 properties.put( IServiceConstants.PERSPECTIVE,
//		             "com.mycom.sendtoapp.perspectives.CustomPerspective" );//perspective id
//		 CustomOpenService customOpenService = new CustomOpenService();
//		
//		 context.registerService( IOpenService.class.getName(),
//		         customOpenService, properties );
//	        
//	 }
}
