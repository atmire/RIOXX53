package org.dspace.project;

import org.dspace.authority.*;
import org.dspace.authority.indexer.AuthorityIndexingService;
import org.dspace.core.Context;
import org.dspace.utils.DSpace;

/**
 * Created by Philip Vissenaekens (philip at atmire dot com)
 * Date: 03/09/15
 * Time: 10:24
 */
public class ProjectService {

    public ProjectAuthorityValue createProject(Context context, String projectId, String funderAuthorityId) {
        AuthorityValueFinder finder = new AuthorityValueFinder();

        AuthorityValue project = finder.findByProjectID(context, projectId);

        if(project!=null){
            throw new IllegalArgumentException("project with id " + projectId + " already exists");
        }

        AuthorityValue funder = finder.findByUID(context, funderAuthorityId);

        if(funder==null){
            throw new IllegalArgumentException("funder with authority id " + funderAuthorityId + " could not be found");
        }

        ProjectAuthorityValue newProject = ProjectAuthorityValue.create();
        newProject.setValue(projectId);
        newProject.setFunderAuthorityValue((FunderAuthorityValue) funder);

        AuthoritySolrServiceImpl solrService = (AuthoritySolrServiceImpl) new DSpace().getServiceManager().getServiceByName(AuthorityIndexingService.class.getName(), AuthorityIndexingService.class);
        solrService.indexContent(newProject, true);

        return newProject;
    }

    public ProjectAuthorityValue getProjectByAuthorityId(Context context, String authorityId){
        AuthorityValueFinder finder = new AuthorityValueFinder();
        AuthorityValue authorityValue = finder.findByUID(context, authorityId);

        if(authorityValue==null || !authorityValue.getAuthorityType().equals("project")) {
            throw new IllegalArgumentException("project with authority id " + authorityId + " could not be found");
        }

        ProjectAuthorityValue projectAuthorityValue = (ProjectAuthorityValue) authorityValue;

        if(projectAuthorityValue.getFunderAuthorityValue()==null){
            throw new IllegalArgumentException("project authority with id " + authorityId + " does not have a valid funder");
        }

        return projectAuthorityValue;
    }
}
