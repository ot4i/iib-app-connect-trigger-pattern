package com.ibm.etools.mft.pattern.hybridintegration.appconnect.code;

import com.ibm.broker.config.appdev.patterns.GeneratePatternInstanceTransform;
import com.ibm.broker.config.appdev.patterns.PatternInstanceManager;




public class PatternGenerator implements GeneratePatternInstanceTransform {
	
	private static final String SAMPLE_ARTIFACTS_PROJECT_NAME = "HTTP-oneway_sample";
	
	// Properties IDs
	private static final String PROPERTY_INCLUDE_SAMPLE_ID = "includesample";
		
	private boolean includeSample;

	@Override
	public void onGeneratePatternInstance(PatternInstanceManager patternInstanceManager) {
		
		// The location for the generated projects 
		String location = patternInstanceManager.getWorkspaceLocation();
		
		// The pattern instance name for this generation
		String patternInstanceName = patternInstanceManager.getPatternInstanceName();
		
		this.includeSample = !patternInstanceManager.getParameterValue(
				PROPERTY_INCLUDE_SAMPLE_ID).equals("sample_none");
	}
}
