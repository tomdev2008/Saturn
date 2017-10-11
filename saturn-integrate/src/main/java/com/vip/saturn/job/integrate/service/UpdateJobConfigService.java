/**
 * 
 */
package com.vip.saturn.job.integrate.service;

import java.util.List;

import com.vip.saturn.job.integrate.entity.JobConfigInfo;

/**
 * 更新Job配置
 * 
 * @author timmy.hu
 */
public interface UpdateJobConfigService {

	/**
	 * 批量更新作业的perferList属性
	 * 
	 * @param jobConfigInfos 作业配置信息
	 */
	void batchUpdatePerferList(List<JobConfigInfo> jobConfigInfos);

}
