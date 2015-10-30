/*
 * Note: Assign DC ticket to PSG
 * Project Key: Samasource Engineering
 * Events: Issue Created
 */
import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.watchers.WatcherManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.comments.CommentManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.util.ImportUtils
import com.atlassian.jira.user.util.UserManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.DelegatingApplicationUser
import com.atlassian.jira.event.type.EventDispatchOption
import com.atlassian.jira.issue.UpdateIssueRequest
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
    def userNames = projectToJiraUserNames[projectNumber.trim()]
    if(userNames == null)
        userNames = [fam:'smenon', pm:'smenon']

    log.debug("user name: $userNames")

    // assign to field account manager
    def fieldAcctMgr = (DelegatingApplicationUser) userManager.getUserByName(userNames['fam'])
    def projectMgr = (DelegatingApplicationUser) userManager.getUserByName(userNames['pm'])
    def issueKey = issue.getKey()
    def issueManager = (IssueManager) ComponentAccessor.getIssueManager()
    def sendMail = true
    def currentUser = ComponentAccessor.getJiraAuthenticationContext().getUser()
    def issueRequest = UpdateIssueRequest.builder().sendMail(true).eventDispatchOption(EventDispatchOption.DO_NOT_DISPATCH).build()

    log.debug("assigning to user: $fieldAcctMgr")
    issue.setAssigneeId(fieldAcctMgr.getKey())
    issueManager.updateIssue(currentUser, issue, issueRequest)
    log.debug("assigned issue:$issueKey to $fieldAcctMgr")

    // add project manager as watcher, if different from FAM
    if (!userNames['fam'].equals(userNames['pm'])) {
        log.debug("adding $projectMgr to watchers list on issue:$issueKey")
        WatcherManager watcherManager = (WatcherManager) ComponentAccessor.getWatcherManager()
        watcherManager.startWatching(projectMgr, issue)
        log.debug("added $projectMgr to watchers list on issue:$issueKey")
    }
}



/*
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
*/
