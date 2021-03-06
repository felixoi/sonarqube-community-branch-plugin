/*
 * Copyright (C) 2021 Michael Clarke
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.github.mc1arke.sonarqube.plugin.server.pullrequest.validator;


import com.github.mc1arke.sonarqube.plugin.InvalidConfigurationException;
import com.github.mc1arke.sonarqube.plugin.almclient.bitbucket.BitbucketClient;
import com.github.mc1arke.sonarqube.plugin.almclient.bitbucket.BitbucketClientFactory;
import org.sonar.api.server.ServerSide;
import org.sonar.db.alm.setting.ALM;
import org.sonar.db.alm.setting.AlmSettingDto;
import org.sonar.db.alm.setting.ProjectAlmSettingDto;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@ServerSide
public class BitbucketValidator implements Validator {

    private final BitbucketClientFactory bitbucketClientFactory;

    public BitbucketValidator(BitbucketClientFactory bitbucketClientFactory) {
        this.bitbucketClientFactory = bitbucketClientFactory;
    }

    @Override
    public List<ALM> alm() {
        return Arrays.asList(ALM.BITBUCKET, ALM.BITBUCKET_CLOUD);
    }

    @Override
    public void validate(ProjectAlmSettingDto projectAlmSettingDto, AlmSettingDto almSettingDto) {
        BitbucketClient bitbucketClient;
        try {
            bitbucketClient = bitbucketClientFactory.createClient(projectAlmSettingDto, almSettingDto);
        } catch (InvalidConfigurationException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            throw new InvalidConfigurationException(InvalidConfigurationException.Scope.PROJECT, "Could not create Bitbucket client - " + ex.getMessage(), ex);
        }
        try {
            bitbucketClient.retrieveRepository(bitbucketClient.resolveProject(almSettingDto, projectAlmSettingDto), bitbucketClient.resolveRepository(almSettingDto, projectAlmSettingDto));
        } catch (IOException | RuntimeException ex) {
            throw new InvalidConfigurationException(InvalidConfigurationException.Scope.PROJECT, "Could not retrieve repository details from Bitbucket - " + ex.getMessage(), ex);
        }
        boolean supportsCodeInsights;
        try {
            supportsCodeInsights = bitbucketClient.supportsCodeInsights();
        } catch (RuntimeException ex) {
            throw new InvalidConfigurationException(InvalidConfigurationException.Scope.PROJECT, "Could not check Bitbucket configuration - " + ex.getMessage(), ex);
        }
        if (!supportsCodeInsights) {
            throw new InvalidConfigurationException(InvalidConfigurationException.Scope.PROJECT, "The configured Bitbucket instance does not support code insights");
        }
    }
}
