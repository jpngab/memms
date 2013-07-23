<%@ page import="org.chai.memms.util.Utils.ReportSubType" %>

<ul class="onecol-checkboxes left">
	<li><g:checkBox name="preventiveOptions" value="nextInterventionScheduledOn" checked="false" />
		<span>${message(code:'listing.report.preventive.schedule.next.interventions.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="percentageInterventionsDone" checked="false" />
		<span>${message(code:'listing.report.preventive.percentage.interventions.done.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="recurrencePeriod" checked="false" /><span>${message(code:'listing.report.preventive.reoccurrence.period.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="startDate" checked="false" /><span>${message(code:'listing.report.preventive.start.date.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="responsible" checked="false" /><span>${message(code:'listing.report.preventive.responsible.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="actionName" checked="false" /><span>${message(code:'listing.report.preventive.action.name.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="actionDescription" checked="false" /><span>${message(code:'listing.report.preventive.action.description.label')}</span></li>
</ul>

<ul class="onecol-checkboxes right">
	<li><g:checkBox name="preventiveOptions" value="location" /><span>${message(code:'listing.report.preventive.location.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="code" /><span>${message(code:'listing.report.preventive.code.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="serialNumber" /><span>${message(code:'listing.report.preventive.serial.number.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="equipmentType" /><span>${message(code:'listing.report.preventive.equipment.type.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="model" /><span>${message(code:'listing.report.preventive.model.label')}</span></li>
	<li><g:checkBox name="preventiveOptions" value="manufacturer" /><span>${message(code:'listing.report.preventive.manufacturer.label')}</span></li>

	<g:if test="${reportSubType == ReportSubType.STATUSCHANGES}">
		<li><g:checkBox name="preventiveOptions" value="statusChanges" /><span>${message(code:'listing.report.preventive.status.changes.label')}</span></li>
	</g:if>
	<li><g:checkBox name="preventiveOptions" value="currentStatus" /><span>${message(code:'listing.report.preventive.current.status.label')}</span></li>
</ul>