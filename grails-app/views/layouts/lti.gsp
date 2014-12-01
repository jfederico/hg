<html>
  <head>
    <title><g:layoutTitle default="HG LTI Broker" /></title>
    <link rel="stylesheet" href="${resource(dir:'css',file:'main.css')}" />
    <link rel="shortcut icon" href="${resource(dir:'images',file:'favicon.ico')}" type="image/x-icon" />
    <r:require modules="bootstrap"/>
    <g:javascript library="application" />
    <r:layoutResources />
    <g:layoutHead />
  </head>
  <body>
    <g:if test="${flash.info}">
    <div class="message" style="display: block">${flash.info}</div>
    </g:if>
    <g:if test="${flash.success}">
    <div class="message_success" style="display: block">${flash.success}</div>
    </g:if>
    <g:if test="${flash.warning}">
    <div class="message_warning" style="display: block">${flash.warning}</div>
    </g:if>
    <g:if test="${flash.error}">
    <div class="message_error" style="display: block">${flash.error}</div>
    </g:if>
    <br/>
    <r:layoutResources />
    <g:layoutBody />		
  </body>	
</html>