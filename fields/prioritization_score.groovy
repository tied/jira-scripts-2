import com.atlassian.jira.ComponentManager
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.IssueManager
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue

CustomFieldManager customFieldManager = ComponentManager.getInstance().getCustomFieldManager()
CustomField fClientRevenue = customFieldManager.getCustomFieldObjectByName('Client Revenue')
CustomField fEngRevenue = customFieldManager.getCustomFieldObjectByName('Revenue for Engineering Work')
CustomField fMeetSLA = customFieldManager.getCustomFieldObjectByName('Unable to Meet SLA Without it')
CustomField fOpEfficiency = customFieldManager.getCustomFieldObjectByName('Operational Efficiency')
CustomField fHubUsability = customFieldManager.getCustomFieldObjectByName('Usability on Hub')

def clientRevenue = issue.getCustomFieldValue(fClientRevenue)
def engRevenue = issue.getCustomFieldValue(fEngRevenue)
def meetSLA = issue.getCustomFieldValue(fMeetSLA)
def opEfficiency = issue.getCustomFieldValue(fOpEfficiency)
def hubUsability = issue.getCustomFieldValue(fHubUsability)

def factor = { fieldValue, f ->
    def value = 0
    if (fieldValue == null) {
        return 0.0d
    }
    if (fieldValue.getValue()[0] == '1') {
        value = 1
    } else if (fieldValue.getValue()[0] == '2') {
        value = 2
    } else if (fieldValue.getValue()[0] == '3') {
        value = 3
    }
    value * f
}

def score = factor(clientRevenue, 0.3d) +
    factor(engRevenue, 0.3d) +
    factor(meetSLA, 0.15d) +
    factor(opEfficiency, 0.15d) +
    factor(hubUsability, 0.1d)
return score.round(2)
