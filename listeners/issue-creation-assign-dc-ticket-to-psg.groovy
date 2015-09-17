/*
 * Note: Assign DC ticket to PSG
 * Project Key: Samasource Engineering
 * Events: Issue Created
 */
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.util.ImportUtils
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.DelegatingApplicationUser
import groovy.json.JsonSlurper

MutableIssue issue = issue
ComponentManager componentManager = ComponentManager.getInstance()
CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager()
CustomField customField = customFieldManager.getCustomFieldObjectByName('diagnostics')

def diagnosticsFieldValue = issue.getCustomFieldValue(customField)
log.debug("diagnostics field value: $diagnosticsFieldValue\n\n")

if (diagnosticsFieldValue) {
    matcher = (diagnosticsFieldValue =~ /hub3.samasource.org\/projects\/(\d+)/)
    if (matcher.find())
        projectNumber = matcher.group(1)

    log.error("project number: $projectNumber")

    def url = "<some url in S3>".toURL()
    def slurper = new JsonSlurper()
    def projectToJiraUserNames = slurper.parse(url)

    log.debug("projectToJiraUserNames:\n\n$projectToJiraUserNames")

    UserManager userManager = (UserManager)  ComponentAccessor.getUserManager()
    def userName = projectToJiraUserNames[projectNumber.trim()]
    if(userName == null)
        userName = 'smenon'

    log.debug("user name: $userName")

    DelegatingApplicationUser theUser = (DelegatingApplicationUser) userManager.getUserByName(userName)

    log.debug("assigning to user: $theUser")

    issue.setAssigneeId(theUser.getKey())
    issue.store()

    def issueKey = issue.getKey()
    log.info("assigned issue:$issueKey to $theUser")
}
