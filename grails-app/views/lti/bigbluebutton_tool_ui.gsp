<html>
    <head>
        <title><g:message code="bigbluebutton.ui.view.title" /></title>
        <meta name="layout" content="lti" />
    </head>
    <body>
        <h1 style="margin-left:20px; text-align: center;">
          <a title="<g:message code="bigbluebutton.ui.view.join" />" class="btn btn-primary btn-large" href="${endpoint_url}?act=ui&cmd=join"><g:message code="bigbluebutton.ui.view.join" /></a>
        </h1>
        <br><br>
        <div class="table-responsive">
        <table class="table table-striped table-bordered table-condensed">
            <thead>
                <tr>
                    <th class="header c0" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.recording" /></th>
                    <th class="header c1" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.activity" /></th>
                    <th class="header c2" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.description" /></th>
                    <th class="header c3" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.date" /></th>
                    <th class="header c4" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.duration" /></th>
                    <g:if test="${data.ismoderator}">
                    <th class="header c5 lastcol" style="text-align:center;" scope="col"><g:message code="bigbluebutton.ui.view.actions" /></th>
                    </g:if>
                </tr>
            </thead>
            <tbody>
            <g:each in="${data.recordings}" var="r">
                <g:if test="${data.ismoderator || r.published == 'true'}">
                <tr class="r0 lastrow">
                    <td class="cell c0" style="text-align:center;">
                    <g:each in="${r.playback}" var="p">
                        <a title="${p.type}" target="_new" href="${p.url}">${p.type}</a>&#32;
                    </g:each>
                    </td>
                    <td class="cell c1" style="text-align:center;">${r.name}</td>
                    <td class="cell c2" style="text-align:center;">xxxxxxxxxxxxxx</td>
                    <td class="cell c3" style="text-align:center;">${new Date( Long.valueOf(r.startTime).longValue() )}</td>
                    <td class="cell c4" style="text-align:center;">${r.duration}</td>
                    <g:if test="${data.ismoderator}">
                    <td class="cell c5 lastcol" style="text-align:center;">
                      <g:if test="${r.published == 'true'}">
                      <button class="btn btn-default btn-xs" name="unpublish_recording" type="submit" value="${r.recordID}" onClick="window.location='${endpoint_url}?act=ui&cmd=unpublish&bbb_recording_published=${r.published}&bbb_recording_id=${r.recordID}'; return false;"><g:message code="bigbluebutton.ui.view.unpublishRecording" /></button>
                      </g:if>
                      <g:else>
                      <button class="btn btn-default btn-xs" name="publish_recording" type="submit" value="${r.recordID}" onClick="window.location='${endpoint_url}?act=ui&cmd=publish&bbb_recording_published=${r.published}&bbb_recording_id=${r.recordID}'; return false;"><g:message code="bigbluebutton.ui.view.publishRecording" /></button>
                      </g:else>
                      <button class="btn btn-danger btn-xs" name="delete_recording" type="submit" value="${r.recordID}" onClick="if(confirm('<g:message code="bigbluebutton.ui.view.deleteRecordingConfirmation" />')) window.location='${createLink(controller:'tool',action:'delete',id: '0')}?bbb_recording_id=${r.recordID}'; return false;"><g:message code="bigbluebutton.ui.view.deleteRecording" /></button>
                    </td>
                    </g:if>
                </tr>
                </g:if>
            </g:each>
            </tbody>
        </table>
        </div>
    </body>
</html>