/**
 * Copyright 2016 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 *  the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 * </p>
 **/

package com.vip.saturn.it.impl;

import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.LongtimeJavaJob;
import com.vip.saturn.job.basic.ShutdownHandler;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.console.domain.JobType;
import com.vip.saturn.job.utils.SystemEnvProperties;
import org.junit.*;
import org.junit.runners.MethodSorters;
import sun.misc.Signal;

import java.util.Collection;

import static org.assertj.core.api.Assertions.fail;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShutdownGracefullyIT extends AbstractSaturnIT {

	@BeforeClass
	public static void setUp() throws Exception {
		startSaturnConsoleList(1);
	}

	@AfterClass
	public static void tearDown() throws Exception {
		stopExecutorListGracefully();
		stopSaturnConsoleList();
	}

	@Before
	public void before() {
		LongtimeJavaJob.statusMap.clear();
	}

	@After
	public void after() {
		LongtimeJavaJob.statusMap.clear();
	}

	@Test
	public void test_A_TERM_Signal() throws Exception {
		SystemEnvProperties.VIP_SATURN_SHUTDOWN_TIMEOUT = 10;
		startExecutorList(1);
		final int shardCount = 3;
		final String jobName = "test_A_TERM_Signal";
		for (int i = 0; i < shardCount; i++) {
			String key = jobName + "_" + i;
			LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
			status.runningCount = 0;
			status.sleepSeconds = 8;
			status.finished = false;
			status.timeout = false;
			LongtimeJavaJob.statusMap.put(key, status);
		}

		JobConfig jobConfig = new JobConfig();
		jobConfig.setJobName(jobName);
		jobConfig.setCron("9 9 9 9 9 ? 2099");
		jobConfig.setJobType(JobType.JAVA_JOB.toString());
		jobConfig.setJobClass(LongtimeJavaJob.class.getCanonicalName());
		jobConfig.setShardingTotalCount(shardCount);
		jobConfig.setShardingItemParameters("0=0,1=1,2=2");
		addJob(jobConfig);
		Thread.sleep(1000);
		enableJob(jobName);
		Thread.sleep(1000);
		runAtOnce(jobName);
		Thread.sleep(50);

		ShutdownHandler.exitAfterHandler(false);
		Signal.raise(new Signal("TERM"));

		try {
			waitForFinish(new FinishCheck() {
				@Override
				public boolean isOk() {

					Collection<LongtimeJavaJob.JobStatus> values = LongtimeJavaJob.statusMap.values();
					for (LongtimeJavaJob.JobStatus status : values) {
						if (!status.finished) {
							return false;
						}
					}
					return true;
				}

			}, 10);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		stopExecutorListGracefully();
	}

	@Test
	public void test_B_INT_Signal() throws Exception {
		SystemEnvProperties.VIP_SATURN_SHUTDOWN_TIMEOUT = 5;
		startExecutorList(1);
		final int shardCount = 3;
		final String jobName = "test_B_INT_Signal";
		for (int i = 0; i < shardCount; i++) {
			String key = jobName + "_" + i;
			LongtimeJavaJob.JobStatus status = new LongtimeJavaJob.JobStatus();
			status.runningCount = 0;
			status.sleepSeconds = 3;
			status.finished = false;
			status.timeout = false;
			LongtimeJavaJob.statusMap.put(key, status);
		}

		JobConfig jobConfig = new JobConfig();
		jobConfig.setJobName(jobName);
		jobConfig.setCron("9 9 9 9 9 ? 2099");
		jobConfig.setJobType(JobType.JAVA_JOB.toString());
		jobConfig.setJobClass(LongtimeJavaJob.class.getCanonicalName());
		jobConfig.setShardingTotalCount(shardCount);
		jobConfig.setShardingItemParameters("0=0,1=1,2=2");
		addJob(jobConfig);
		Thread.sleep(1000);
		enableJob(jobName);
		Thread.sleep(1000);
		runAtOnce(jobName);
		Thread.sleep(50);

		ShutdownHandler.exitAfterHandler(false);
		Signal.raise(new Signal("INT"));

		try {
			waitForFinish(new FinishCheck() {
				@Override
				public boolean isOk() {

					Collection<LongtimeJavaJob.JobStatus> values = LongtimeJavaJob.statusMap.values();
					for (LongtimeJavaJob.JobStatus status : values) {
						if (!status.finished) {
							return false;
						}
					}
					return true;
				}

			}, 5);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

		stopExecutorListGracefully();
	}
}
