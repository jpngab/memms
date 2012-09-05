<div class="row date ${hasErrors(bean:bean,field:field,'errors')}">
	<label>${label}</label>
	<g:datePicker name="${name}" value="${value}" default="none"  noSelection="['':' ']"  precision="${precision}"   years="${Calendar.getInstance().get(Calendar.YEAR)+10..1950}"/>
	<div class="error-list"><g:renderErrors bean="${bean}" field="${field}" /></div>
</div>