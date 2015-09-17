import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager()
CustomField customField_name = customFieldManager.getCustomFieldObjectByName('Hub Project Number')

def projectIds = (issue.getCustomFieldValue(customField_name))
if (projectIds != null && !projectIds.isEmpty()) {
    def style = "font-size: smaller; width: 6em; color: #fff; box-sizing: border-box; background-color: #337ab7; border-color: #2e6da4; padding: 6px 12px; cursor: pointer; border-radius: 4px; border: 1px solid transparent; text-align: center;"
    def html = ""
    for(project_id in projectIds) {
        if (project_id != null) {
            def link = "https://hub3.samasource.org/projects/" + project_id
            def project_settings = link + "#settings/overview"
            def project_status = link + "/status"
            html = html +
                "<a style='" + style + "' href='" +
                project_status +
                "'>" + project_id + " Status</a> " +
                "<a style='" + style + "' href='" +
                project_settings +
                "'>" + project_id + " Settings</a> <br /> <br />"
        }
    }
    return html
}
else {
  return ""
}
